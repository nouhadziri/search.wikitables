<html>
    <head>
        <title>Wikitables Search</title>

        <link href="css/bootstrap.min.css" rel="stylesheet" />
    </head>
    <body>
        <#if errors?has_content>
            <div class="container">
                <h6 class="text-danger">${errors}</h6>
            </div>
        </#if>

        <form class="form-inline" method="GET" action="/search">
            <div class="form-group">
                <label for="keyword">Keyword</label>
                <input type="text"
                       class="form-control"
                       id="keyword"
                       name="q"
                       placeholder="Enter a text to search">
            </div>
            <button type="submit" class="btn btn-default">Search</button>
        </form>

        <#if results?has_content>
            <div class="container">
                <#list results as item>
                    <div class="row">
                        Table #${item.getTableIndex()} in <a href="${item.getUrl()}">${item.getTitle()}</a>
                        <br/>
                    </div>
                </#list>
            </div>
        </#if>
    <body>
</html>