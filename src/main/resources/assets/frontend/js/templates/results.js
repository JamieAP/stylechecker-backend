<script id="results-template" type="text/handlebars-template">
    <div class="bs-callout bs-callout-primary">
        <h4>Report</h4>
        <ul class="list-group">
            <li class="list-group-item">
                <span class="badge">{{numberOfChecks}}</span>
                Number of Checks:
            </li>
            <li class="list-group-item">
                <span class="badge">{{uniqueFailedChecks}}</span>
                Distinct Failed Checks:
            </li>
            <li class="list-group-item">
                <span class="badge">{{totalFailedChecks}}</span>
                Total Failed Checks:
            </li>
        </ul>
    </div>
    <div class="bs-callout bs-callout-primary">
        <h4>Ignored Files <span class="badge">{{numIgnoredFiles}}</span></h4>
        {{#each ignoredFiles}}
            {{this}}
        {{/each}}
    </div>
</script>
