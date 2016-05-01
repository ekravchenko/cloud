/**
 * Function provides access to the array that we are going to sort
 *
 * @returns {string[]} - Array that should be sorted
 */
function arrayToSort() {
    return cloud.get('uawebchallenge');
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
    library.require("printArray");

    var array = arrayToSort();

    log.info("-------------------------------");
    log.info("Dividing array into sub arrays.");
    log.debug("Slicing array: " + printArray(array));

    var bucketSize = 80;
    var buckets = array.length / bucketSize;

    log.debug("Size of sub array: " + bucketSize);

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

        log.info("Created task to slice array in range: " + [startIndex, endIndex]);
        log.debug("Task id: " + taskId);
    }

    var oddSortTask = {
        input: tasks,
        script: function main(context) {
            library.require("oddSort");
            return oddSort(context);
        },
        dependsOn: tasks,
        parentId: context.taskId
    };

    cloud.put(context.taskId, false);
    var taskId = cloud.createTask(oddSortTask);

    log.info("Created task to start 'odd' sort when slicing is completed");
    log.debug("Task id: " + taskId);
    log.debug("Task depends on: " + tasks);
    log.info("Finished 'divideIntoBuckets' task");
    log.info("-------------------------------");
}

/**
 * Slicing array is simple task that creates sub-part of arrayToSort. This task can be run in parallel.
 *
 * @param context - task execution context object. it gets the following data: taskID, parentId (parent task ID), input
 * (input data), dependsOn (task dependencies on other tasks)
 */
function slice(context) {
    library.require("arrayToSort");

    var startIndex = context.input[0];
    var endIndex = context.input[1];
    log.info("-------------------------------");
    log.info("Creating sliced buckets");
    log.info("startIndex: " + startIndex);
    log.info("endIndex: " + endIndex);

    var result = arrayToSort().slice(startIndex, endIndex);
    log.debug("result: [" + result + "]");

    cloud.put(context.taskId, result);
    log.info("Sub array was successfully created");
    log.info("-------------------------------");
}

/**
 * Sorting pairs of sub-array starting from odd index. This task will not do sorting but will rather create sub tasks
 * to do that. At last it will create a sub task to check results and run another round of sorting if that is needed
 *
 * @param context - task execution context object. it gets the following data: taskID, parentId (parent task ID), input
 * (input data), dependsOn (task dependencies on other tasks)
 *
 * @returns {*} true/false - result of previous round of sorting (result of previous check)
 */
function oddSort(context) {
    log.info("-------------------------------");
    log.info("Starting odd sorting");

    var bucketAddresses = context.input;

    var sortTasks = [];

    for (var i = 1; i < bucketAddresses.length - 1; i = i + 2) {
        var address1 = bucketAddresses[i];
        var address2 = bucketAddresses[i + 1];

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

        log.info("Created task to sort pairs %s and %s", i, i + 1);
        log.debug("Task id:" + taskId);
    }

    var checkResultTask = {
        input: context.input,
        script: function main(context) {
            library.require("checkSortResult");
            return checkSortResult(context, true);
        },
        dependsOn: sortTasks,
        parentId: context.taskId
    };

    var taskId = cloud.createTask(checkResultTask);
    log.info("Created task to check odd sorting results");
    log.debug("Task id:" + taskId);
    log.debug("Task depends on:" + sortTasks);
    log.info("Finished odd sorting");
    log.info("-------------------------------");

    // Result of the previous round of sorting is returned. Current odd sorting result will be known is 'checkResults'
    return cloud.get(context.parentId);
}

/**
 * Almost same as odd sorting. The only difference is that it start from even indexes
 *
 * @param context - task execution context object. it gets the following data: taskID, parentId (parent task ID), input
 * (input data), dependsOn (task dependencies on other tasks)
 *
 * @returns {*} true/false result of previous round
 */
function evenSort(context) {
    log.info("-------------------------------");
    log.info("Starting even sorting");
    var bucketAddresses = context.input;

    var sortTasks = [];

    for (var i = 0; i < bucketAddresses.length - 1; i = i + 2) {
        var address1 = bucketAddresses[i];
        var address2 = bucketAddresses[i + 1];

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

        log.info("Created task to sort pairs %s and %s", i, i + 1);
        log.debug("Task id:" + taskId);
    }

    var checkResultTask = {
        input: context.input,
        script: function main(context) {
            library.require("checkSortResult");
            return checkSortResult(context, false);
        },
        dependsOn: sortTasks,
        parentId: context.taskId
    };

    var taskId = cloud.createTask(checkResultTask);
    log.info("Created task to check even sorting results");
    log.debug("Task id:" + taskId);
    log.debug("Task depends on:" + sortTasks);
    log.info("Finished even sorting");
    log.info("-------------------------------");

    return cloud.get(context.parentId);
}

/**
 * This task can be run concurrently. It merges 2 arrays, sorts them and slices them back
 * @param context
 *
 * @param context - task execution context object. it gets the following data: taskID, parentId (parent task ID), input
 * (input data), dependsOn (task dependencies on other tasks)
 *
 * @returns {*} returns answer to the question - Did sorting actually happen? YES or NO?
 */
function sortPairs(context) {
    library.require("arraysEquals");
    library.require("printArray");

    log.info("-------------------------------");
    log.info("Sorting arrays...");

    var address1 = context.input[0];
    var address2 = context.input[1];

    var array1 = cloud.get(address1);
    var array2 = cloud.get(address2);

    log.debug("Array1: " + printArray(array1));
    log.debug("Array2: " + printArray(array2));

    var arrayToSort = array1.concat(array2);
    var arrayToSortCopy = array1.concat(array2);

    arrayToSort.sort();
    log.debug("Merged array (after sorting): " + printArray(arrayToSort));
    log.debug("Merged array (before sorting): " + printArray(arrayToSortCopy));

    var noSorting = arraysEquals(arrayToSort, arrayToSortCopy);
    log.info("Arrays are already sorted?: " + noSorting);

    var array1 = arrayToSort.slice(0, arrayToSort.length >> 1);
    var array2 = arrayToSort.slice(arrayToSort.length >> 1);

    log.debug("Array1 (after slice): " + printArray(array1));
    log.debug("Array2 (after slice): " + printArray(array2));

    cloud.put(address1, array1);
    cloud.put(address2, array2);

    log.info("Sorting is completed");
    log.info("-------------------------------");
    return noSorting;
}

/**
 * Check results of sorting and if needed start a new round of sorting. This task depends on sorting tasks and it should
 * be the last task in even/odd round
 *
 * @param context - task execution context object. it gets the following data: taskID, parentId (parent task ID), input
 * (input data), dependsOn (task dependencies on other tasks)
 *
 * @param even - flag that helps figure out if next round of sorting should be odd or even
 *
 * @returns {boolean} - result of this round of sorting
 */
function checkSortResult(context, even) {
    library.require("printArray");
    library.require("addArray");

    log.info("-------------------------------");
    log.info("Check sort results");

    var sortTasks = context.dependsOn;

    var allAlreadySorted = true;
    for (var i = 0; i < sortTasks.length; i++) {
        var sortTaskId = sortTasks[i];
        var alreadySorted = cloud.get(sortTaskId);
        allAlreadySorted = allAlreadySorted && alreadySorted;
    }

    log.info("In this round sorting didn't happen?: " + allAlreadySorted);

    var previousSortingResult = cloud.get(context.parentId);
    log.info("In previous round sorting didn't happen?:" + previousSortingResult);


    if (previousSortingResult && allAlreadySorted) {
        log.info("Everything is sorted congratulations!");

        var finalArray = [];

        for (var i = 0; i < context.input.length; i++) {
            var sorted = cloud.get(context.input[i]);
            addArray(finalArray, sorted);
        }

        var topParentId = cloud.topParentId(context.taskId);
        log.debug("Top parent id: " + topParentId);
        log.debug("Sorted array: " + finalArray);
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

        var taskId = cloud.createTask(evenSorting);
        log.info("We need to run one more round of even sorting");
        log.debug("Task id: " + taskId);
    } else {
        var oddSorting = {
            input: context.input,
            script: function main(context) {
                library.require("oddSort");
                return oddSort(context);
            },
            parentId: context.taskId
        };

        var taskId = cloud.createTask(oddSorting);
        log.info("We need to run one more round of odd sorting");
        log.debug("Task id: " + taskId);
    }

    log.info("Done checking sort results");
    log.info("-------------------------------");

    return allAlreadySorted;
}

/**
 * Helper method to convert array to string. The problem is that when array comes from Java it is treated by ScriptEngine
 * as non-native JS array. However methods like concat and slice are still there - transforming it to string is a problem
 *
 * @param array - array that should be transformed to string
 * @returns {string} - string that represents array
 */
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

/**
 * Concatenate 2 arrays
 * @param array - main array that will get second array added to
 * @param extra - array to add to the main array
 */
function addArray(array, extra) {
    for (var i = 0; i < extra.length; i++) {
        array.push(extra[i]);
    }
}

/**
 * Check if 2 arrays are equals
 *
 * @param array1 - first array
 * @param array2 - second array
 * @returns {boolean} - true if arrays are equals
 */
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