function arrayToSort() {
    return ["true", "goal", "working", "trouble", "awesome", "fun", "dangerous", "understand", "mine"];
}

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
        tasks[i] = taskId;
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

        cloud.createTask(sortTask);

        // TODO Add tasks for the array
    }

    // TODO Create task to check result!
    return cloud.get(context.parentId);
}

function evenSort(context) {

}

function sortPairs(context) {
    var address1 = context.input[0];
    var address2 = context.input[1];

    var array1 = cloud.get(address1);
    var array2 = cloud.get(address2);
}

function checkSortResult() {

}

function main(context) {
    library.export("arrayToSort", arrayToSort);
    library.export("divideIntoBuckets", divideIntoBuckets);
    library.export("slice", slice);
    library.export("oddSort", oddSort);
    library.export("sortPairs", sortPairs);

    var task = {
        input: null,
        script: function main(context) {
            library.require("divideIntoBuckets");
            return divideIntoBuckets(context);
        },
        parentId: context.taskId,
        dependsOn: null
    };

    var taskId = cloud.createTask(task);
}