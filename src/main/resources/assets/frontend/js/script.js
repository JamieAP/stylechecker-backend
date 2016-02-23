Handlebars.registerHelper('firstLine', function (line, opts) {
    return line - 2;
});

Handlebars.registerHelper('secondLine', function (line, opts) {
    return line - 1;
});

Handlebars.registerHelper('forthLine', function (line, opts) {
    return line + 1;
});

Handlebars.registerHelper('fifthLine', function (line, opts) {
    return line + 2;
});

Handlebars.registerHelper('failedChecksForFile', function (auditEntries) {
    return auditEntries.length;
});

$(document).on('change', '.btn-file :file', function () {
    var input = $(this),
        numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
    input.trigger('fileselect', [numFiles, label]);
});

$(document).ready(function () {
    $('#uploadProgress > *').hide();
    $("#upload").submit(function (event) {
        event.preventDefault();
        var file = $(this)[0][0].files[0];
        var formData = new FormData();
        var acceptedExtensions = [".jar", ".zip"];
        formData.append('file', file);

        if (_.find(acceptedExtensions, function(ext) {return file.name.toLowerCase().endsWith(ext)})) {
            $('#uploadForm > *').hide();
            $('#uploadProgress > *').show();
            submitForm(formData);
        } else {
            alert("Please select a .zip or .jar file");
        }
    });
});

function submitForm(formData) {
    $.ajax({
        xhr: function()
        {
            var xhr = new window.XMLHttpRequest();
            //Upload progress
            xhr.upload.addEventListener("progress", function(evt){
                if (evt.lengthComputable) {
                    var percentComplete = (evt.loaded / evt.total) * 100;
                    $('.progress-bar').css('width', percentComplete+'%').attr('aria-valuenow', percentComplete);
                }
            }, false);
            return xhr;
        },
        url: '/stylechecker/api/check',
        type: 'POST',
        data: formData,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (returndata) {
            $('#uploadProgress > *').hide();
            $('#kentLogo > *').hide();
            printResults(returndata);
            test(returndata);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log("Status: " + textStatus);
            console.log("Error: " + errorThrown);
            $('#uploadProgress > *').hide();
            $('#uploadForm > *').show();
            alert("There was a problem processing your file, Please try again later");
        },
    });
}

function printResults(jsonData) {
    var src = $("#results-template").html();
    var template = Handlebars.compile(src);
    var processedFiles = _.map(jsonData.fileAudits, function (a) {
        return a.filePath;
    })

    var data = {
        numberOfChecks: jsonData.numberOfChecks,
        uniqueFailedChecks: jsonData.uniqueFailedChecks,
        totalFailedChecks: jsonData.totalFailedChecks,
        ignoredFiles: jsonData.ignoredFiles,
        numIgnoredFiles: jsonData.ignoredFiles.length,
        grade: 100 - (jsonData.uniqueFailedChecks / jsonData.numberOfChecks).toFixed(2) * 100,
        processedFiles: processedFiles,
        numProcessedFiles: processedFiles.length,
        fileAudits: jsonData.fileAudits
    };
    $("#result").html(template(data));
}

function test(jsonData) {

    //Unique Audits + Counts
    var auditEntries = new Array();
    _.forEach(jsonData.fileAudits, function (fileAudit) {
        _.forEach(fileAudit.auditEntries, function (auditEntry) {
            auditEntries.push(auditEntry.styleGuideRule);
        });
    });
    var auditEntryCounts = new Map();
    for (var i = 0; i < auditEntries.length; i++) {
        auditEntryCounts.set(auditEntries[i], auditEntryCounts.get(auditEntries[i]) == undefined ? 1 : auditEntryCounts.get(auditEntries[i]) + 1)
    }
    auditEntryCounts.forEach(function (value, key) {
        console.log(key + " = " + value);
    }, auditEntryCounts)

    //Audits per files + Counts
    var fileAuditEntryCounts = new Map();
    _.forEach(jsonData.fileAudits, function (fileAudit) {
        fileAuditEntryCounts.set(fileAudit.filePath, fileAudit.auditEntries.length)
    });
    fileAuditEntryCounts.forEach(function (value, key) {
        console.log(key + " = " + value);
    }, fileAuditEntryCounts)
}