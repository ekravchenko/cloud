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
                return slice(context);
            },
            parentId: null,
            dependsOn: null
        };

        var taskId = cloud.createTask(task);
        tasks[i] = taskId;
    }

    var resultTask = {
        input: tasks,
        script: function main(context) {
            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!");
        },
        dependsOn: tasks,
        parentId: context.taskId
    };

    cloud.createTask(resultTask);
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

    return result;
}

function main(context) {
    library.export("arrayToSort", arrayToSort);
    library.export("divideIntoBuckets", divideIntoBuckets);
    library.export("slice", slice);

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