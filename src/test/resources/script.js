var arrayToSort = ["true", "goal", "working", "trouble", "awesome", "fun", "dangerous", "understand", "mine"];

function divideIntoBuckets(context) {
    log.trace("Slicing array=[" + arrayToSort + "]");
    var bucketSize = 4;
    var buckets = arrayToSort.length / bucketSize;
    log.trace("buckets=" + buckets);

    for (var i = 0; i < buckets; i++) {
        var startIndex = i * bucketSize;
        var endIndex = startIndex + bucketSize;

        var task = {
            input: [startIndex, endIndex],
            script: function main(context) {
                var arrayToSort = ["true", "goal", "working", "trouble", "awesome", "fun", "dangerous", "understand", "mine"];
                var startIndex = context.input[0];
                var endIndex = context.input[1];
                log.trace("Creating sliced buckets");
                log.trace("startIndex=" + startIndex);
                log.trace("endIndex=" + endIndex);
                var result = arrayToSort.slice(startIndex, endIndex);
                log.trace("result=[" + result + "]");
            },
            parentId: null,
            dependsOn: null
        };

        var taskId = cloud.createTask(task);
    }
}

function main(context) {


    var task = {
        input: null,
        script: function main(context) {
            return divideIntoBuckets(context);
        },
        parentId: context.taskId,
        dependsOn: null
    };

    var taskId = cloud.createTask(task);
}