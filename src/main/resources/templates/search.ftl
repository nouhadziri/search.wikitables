<!DOCTYPE html>

<html lang="en">
    <head>
        <title>Wikitables Search</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <link href="css/bootstrap.min.css" rel="stylesheet" />
    </head>
    <body>
        <div class="container">

            <div class="container">
                <ul class="nav nav-tabs" role="tablist">
                    <li class="active"><a href="#">Keyword Search</a></li>
                    <li><a href="/cinit">Category Search</a></li>
                    <li><a href="/rinit">Relationship Search</a></li>
                </ul>
            </div>

           <#if errors?has_content>
               <p class="text-danger">${errors}</p>
           </#if>

           <form class="form-inline" method="GET" action="/search">
               <div class="input-group">
                <input type="text" class="form-control" value="${query!}" name="q" placeholder="Enter a text to Search">
                <div class="input-group-btn">
                    <button class="btn btn-default" type="submit">
                        <i class="glyphicon glyphicon-search"></i>
                    </button>
                </div>
               </div>
           </form>
        </div>


        <#if results?has_content>
            <div class="container">
                <#list results as item>
                    <div class="row">
                        <p>
                            Table #${item.getTableIndex()} in <a class="btn-link" href="${item.getUrl()}">${item.getTitleForUI()}</a>
                        </p>

                        <table class="table table-striped table-bordered">
                            <#if item.getHeaders()?size gt 0>
                                <thead>
                                    <tr>
                                        <#list item.getHeaders() as header>
                                            <th>${header}</th>
                                        </#list>
                                    </tr>
                                </thead>
                            </#if>
                            <tbody>
                                <#list item.getContents() as content>
                                    <tr>
                                        <#list content.getValues() as col>
                                            <td>${col}</td>
                                        </#list>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </#list>
                <ul class="pager">
                    <#if previous?has_content>
                        <li><a href="/search?q=${query}&_s=${previous}">Previous</a></li>
                    </#if>
                    <li><a href="/search?q=${query}&_s=${next}">Next</a></li>
                  </ul>
            </div>
        </#if>
    <body>
</html>