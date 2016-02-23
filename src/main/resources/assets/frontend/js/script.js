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

        if (_.find(acceptedExtensions, function (ext) {
                return file.name.toLowerCase().endsWith(ext)
            })) {
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
        xhr: function () {
            var xhr = new window.XMLHttpRequest();
            //Upload progress
            xhr.upload.addEventListener("progress", function (evt) {
                if (evt.lengthComputable) {
                    var percentComplete = (evt.loaded / evt.total) * 100;
                    $('.progress-bar').css('width', percentComplete + '%').attr('aria-valuenow', percentComplete);
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
            prepareRulesAuditChartData(returndata);
            prepareFilesAuditChartData(returndata);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("Status: " + textStatus);
            console.log("Error: " + errorThrown);
            $('#uploadProgress > *').hide();
            $('#uploadForm > *').show();
            alert("There was a problem processing your file, Please try again later");
        }
    });
}

function printResults(jsonData) {
    var src = $("#results-template").html();
    var template = Handlebars.compile(src);
    var processedFiles = _.map(jsonData.fileAudits, function (a) {
        return a.filePath;
    });

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

function prepareRulesAuditChartData(jsonData) {
    var brokenRules = _.flatMap(jsonData.fileAudits, function (fileAudit) {
        return _.map(fileAudit.auditEntries, function (auditEntry) {
            return auditEntry.styleGuideRule;
        });
    });

    var brokenRulesToCounts = _.countBy(brokenRules, function (rule) {
        return rule;
    });

    var chartData = _.map(_.keys(brokenRulesToCounts), function (key) {
        return {
            name: key,
            y: brokenRulesToCounts[key]
        }
    });
    drawRulesAuditChart(chartData);
}

function drawRulesAuditChart(chartData) {
    $('#rulesAuditChart').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        },
        title: {
            text: 'Analysis by Style Guide rules'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    },
                    formatter: function() {
                        return this.point.name.substring(0, 3);
                    }
                }
            }
        },
        series: [{
            name: '# of failures',
            colorByPoint: true,
            data: chartData
        }]
    });
}

function prepareFilesAuditChartData(jsonData) {
    var entriesPerFile = {};
    _.forEach(jsonData.fileAudits, function (fileAudit) {
        entriesPerFile[fileAudit.filePath] = fileAudit.auditEntries.length;
    });
    
    var chartData = _.map(_.keys(entriesPerFile), function (key) {
        return {
            name: key,
            y: entriesPerFile[key]
        }
    });
    drawFilesAuditChart(chartData);
}

function drawFilesAuditChart(chartData) {
    $('#filesAuditChart').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        },
        title: {
            text: 'Analysis by file'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    },
                    formatter: function() {
                        return this.point.y
                    }
                }
            }
        },
        series: [{
            name: '# of failures',
            colorByPoint: true,
            data: chartData
        }]
    });
}