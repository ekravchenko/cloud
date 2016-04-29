/**
 * Function provides access to the array that we are going to sort
 *
 * @returns {string[]} - Array that should be sorted
 */
function arrayToSort() {
    return ["true", "goal", "working", "trouble", "awesome", "fun", "dangerous", "understand", "mine"];
}

/**
 * Function that helps break array into smaller arrays (sub arrays or buckets). The trick is that we are not going to
 * slice array into smaller parts in this task but rather we will create tasks to slice specific part. This tasks will
 * run in parallel.
 *
 * @param context - task execution context object. it gets the following data: taskID, parentId (parent task ID), input
 * (input data), dependsOn (task dependencies on other tasks)
 */
function divideIntoBuckets(context) {
    library.require("arrayToSort");

    log.trace("Slicing array=[" + arrayToSort() + "]");
    var bucketSize = 4;
    var buckets = arrayToSort().length / bucketSize;
    log.trace("buckets=" + buckets);

    var tasks = [];

    for (var i = 0; i < buckets; i++) {
        var startIndex = i * bucketSize;
        var endIndex = startIndex + bucketSize;

        var task = {
            input: [startIndex, endIndex],
            script: function main(context) {
                library.require("slice");
                slice(context);
            },
            parentId: null,
            dependsOn: null
        };

        var taskId = cloud.createTask(task);
        tasks.push(taskId);
    }

    cloud.put(context.taskId, false);

    var oddSortTask = {
        input: tasks,
        script: function main(context) {
            library.require("oddSort");
            return oddSort(context);
        },
        dependsOn: tasks,
        parentId: context.taskId
    };

    cloud.createTask(oddSortTask);
}

function slice(context) {
    library.require("arrayToSort");

    var startIndex = context.input[0];
    var endIndex = context.input[1];
    log.trace("Creating sliced buckets");
    log.trace("startIndex=" + startIndex);
    log.trace("endIndex=" + endIndex);

    var result = arrayToSort().slice(startIndex, endIndex);
    log.trace("result=[" + result + "]");

    cloud.put(context.taskId, result);
}

function oddSort(context) {
    log.trace("Starting odd sorting...");
    var bucketAddresses = context.input;

    var sortTasks = [];

    for (var i = 1; i < bucketAddresses.length; i = i + 2) {
        var address1 = bucketAddresses[i];
        var address2 = bucketAddresses[i + 1];

        log.trace("Scheduling task to sort pairs:");
        log.trace("index=%s address=%s", i, address1);
        log.trace("index=%s address=%s", i + 1, address2);

        var sortTask = {
            input: [address1, address2],
            script: function main(context) {
                library.require("sortPairs");
                return sortPairs(context);
            },
            parentId: context.taskId
        };

        var taskId = cloud.createTask(sortTask);
        sortTasks.push(taskId);
    }

    log.trace("Sorting tasks:" + sortTasks);

    var checkResultTask = {
        input: context.input,
        script: function main(context) {
            library.require("checkSortResult");
            return checkSortResult(context, true);
        },
        dependsOn: sortTasks,
        parentId: context.taskId
    };

    log.trace("Scheduling task to check results.");
    cloud.createTask(checkResultTask);

    return cloud.get(context.parentId);
}

function evenSort(context) {
    log.trace("Starting even sorting...");
    var bucketAddresses = context.input;

    var sortTasks = [];

    log.trace("Input length=" + context.input.length);

    for (var i = 0; i < bucketAddresses.length - 1; i = i + 2) {
        var address1 = bucketAddresses[i];
        var address2 = bucketAddresses[i + 1];

        log.trace("Scheduling task to sort pairs:");
        log.trace("index=%s address=%s", i, address1);
        log.trace("index=%s address=%s", i + 1, address2);

        var sortTask = {
            input: [address1, address2],
            script: function main(context) {
                library.require("sortPairs");
                return sortPairs(context);
            },
            parentId: context.taskId
        };

        var taskId = cloud.createTask(sortTask);
        sortTasks.push(taskId);
    }

    log.trace("Sorting tasks:" + sortTasks);

    var checkResultTask = {
        input: context.input,
        script: function main(context) {
            library.require("checkSortResult");
            return checkSortResult(context, false);
        },
        dependsOn: sortTasks,
        parentId: context.taskId
    };

    log.trace("Scheduling task to check results.");
    cloud.createTask(checkResultTask);

    return cloud.get(context.parentId);
}

function sortPairs(context) {
    library.require("arraysEquals");
    library.require("printArray");

    var address1 = context.input[0];
    var address2 = context.input[1];

    var array1 = cloud.get(address1);
    var array2 = cloud.get(address2);

    log.debug("Sorting arrays");
    log.trace("Array1: " + printArray(array1));
    log.trace("Array2: " + printArray(array2));

    var arrayToSort = array1.concat(array2);
    var arrayToSortCopy = array1.concat(array2);

      log.trace("ArrayToSort: " + printArray(arrayToSort));
    log.trace("ArrayToSortCopy: " + printArray(arrayToSortCopy));

    arrayToSort.sort();
    log.trace("Array (after sorting): " + printArray(arrayToSort));
    log.trace("Array (before sorting): " + printArray(arrayToSortCopy));

    var noSorting = arraysEquals(arrayToSort, arrayToSortCopy);
    log.trace("Sorting didn't happen?: " + noSorting);

    var array1 = arrayToSort.slice(0, arrayToSort.length >> 1);
    var array2 = arrayToSort.slice(arrayToSort.length >> 1);

    log.trace("Array1 (after slice): " + printArray(array1));
    log.trace("Array2 (after slice): " + printArray(array2));

    cloud.put(address1, array1);
    cloud.put(address2, array2);

    return noSorting;
}

function arraysEquals(array1, array2) {
    if (array1.length !== array2.length) {
        log.info("Arrays have different length");
        return false;
    }

    for (var i = 0; i < array1.length; i++) {
        if (array1[i] !== array2[i]) {
            return false;
        }
    }

    return true;
}

function checkSortResult(context, even) {
    library.require("printArray");
    library.require("addArray");

    var sortTasks = context.dependsOn;
    log.trace("Check sorting results");

    var allAlreadySorted = true;
    for (var i = 0; i < sortTasks.length; i++) {
        var sortTaskId = sortTasks[i];
        var alreadySorted = cloud.get(sortTaskId);
        log.trace("TaskId: " + sortTaskId);
        log.trace("Sorting didn't happen?:" + alreadySorted);
        allAlreadySorted = allAlreadySorted && alreadySorted;
    }

    log.trace("Sorting didn't happen for all tasks?:" + allAlreadySorted);

    var previousSortingResult = cloud.get(context.parentId);
    log.trace("Round before sorting didn't happen?:" + previousSortingResult);


    if (previousSortingResult && allAlreadySorted) {
        log.info("Everything is sorted congratulations!");

        var finalArray = [];

        for (var i = 0; i < context.input.length; i++) {
            var sorted = cloud.get(context.input[i]);
            addArray(finalArray, sorted);
        }

        var topParentId = cloud.topParentId(context.taskId);
        log.info("Before puttin!");
        log.info(finalArray);
        cloud.put(topParentId, finalArray);
    }
    else if (even) {
        var evenSorting = {
            input: context.input,
            script: function main(context) {
                library.require("evenSort");
                return evenSort(context);
            },
            parentId: context.taskId
        };

        cloud.createTask(evenSorting);
    } else {
        var oddSorting = {
            input: context.input,
            script: function main(context) {
                library.require("oddSort");
                return oddSort(context);
            },
            parentId: context.taskId
        };

        cloud.createTask(oddSorting);
    }

    return allAlreadySorted;
}

function printArray(array) {
    var str = "";
    for (var i = 0; i < array.length; i++) {
        if (i == 0) {
            str = str + "["
        }
        str = str + array[i];
        if (i != array.length - 1) {
            str = str + ", ";
        }
        else {
            str = str + "]"
        }
    }
    return str;
}

function addArray(array, extra) {
    for (var i = 0; i < extra.length; i++) {
        array.push(extra[i]);
    }
}

function main(context) {
    library.export("arrayToSort", arrayToSort);
    library.export("divideIntoBuckets", divideIntoBuckets);
    library.export("slice", slice);
    library.export("oddSort", oddSort);
    library.export("evenSort", evenSort);
    library.export("sortPairs", sortPairs);
    library.export("printArray", printArray);
    library.export("arraysEquals", arraysEquals);
    library.export("checkSortResult", checkSortResult);
    library.export("addArray", addArray);

    var task = {
        input: null,
        script: function main(context) {
            library.require("divideIntoBuckets");
            return divideIntoBuckets(context);
        },
        parentId: context.taskId,
        dependsOn: null
    };

    cloud.createTask(task);
}