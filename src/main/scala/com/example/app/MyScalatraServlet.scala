package com.example.app

import org.scalatra._
import scala.xml.{Text, Node}
import scalate.ScalateSupport
import servlet.{MultipartConfig, SizeConstraintExceededException, FileUploadSupport}

class MyScalatraServlet extends MyScalatraWebAppStack with FlashMapSupport with FileUploadSupport with ScalateSupport {

  private def displayPage(title:String, content:Seq[Node]) = Template.page(title, content, url(_))

  get("/") {
    <html>
      <body>
        <h1>Hello, world again!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/upload") {
    displayPage("File upload example",
      <form action={url("/upload")} method="post" enctype="multipart/form-data">
        <p>File to upload: <input type="file" name="file" /></p>
        <p><input type="submit" value="Upload" /></p>
      </form>
        <p>
          Upload a file using the above form. After you hit "Upload"
          the file will be uploaded and your browser will start
          downloading it.
        </p>

        <p>
          The maximum file size accepted is 3 MB.
        </p>)
  }

  post("/upload") {
    fileParams.get("file") match {
      case Some(file) =>
        Ok(file.get(), Map(
          "Content-Type"        -> (file.contentType.getOrElse("application/octet-stream")),
          "Content-Disposition" -> ("attachment; filename=\"" + file.name + "\"")
        ))

      case None =>
        BadRequest(displayPage("No File",
          <p>
            Hey! You forgot to select a file.
          </p>))
    }
  }

  get("/date/:year/:month/:day") {
    displayPage("Scalatra: Date Example",
      <ul>
        <li>Year: {params("year")}</li>
        <li>Month: {params("month")}</li>
        <li>Day: {params("day")}</li>
      </ul>
        <pre>Route: /date/:year/:month/:day</pre>
    )
  }

  get("/form") {
    displayPage("Scalatra: Form Post Example",
      <form action={url("/post")} method='POST'>
        Post something: <input name="submission" type='text'/>
        <input type='submit'/>
      </form>
        <pre>Route: /form</pre>
    )
  }

  post("/post") {
    displayPage("Scalatra: Form Post Result",
      <p>You posted: {params("submission")}</p>
        <pre>Route: /post</pre>
    )
  }

}

object Template {

  def page(title:String, content:Seq[Node], url: String => String = identity _, head: Seq[Node] = Nil, scripts: Seq[String] = Seq.empty, defaultScripts: Seq[String] = Seq("/assets/js/jquery.min.js", "/assets/js/bootstrap.min.js")) = {
    <html lang="en">
      <head>
        <title>{ title }</title>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="" />
        <meta name="author" content="" />

        <!-- Le styles -->
        <link href="/assets/css/bootstrap.css" rel="stylesheet" />
        <link href="/assets/css/bootstrap-responsive.css" rel="stylesheet" />
        <link href="/assets/css/syntax.css" rel="stylesheet" />
        <link href="/assets/css/scalatra.css" rel="stylesheet" />

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
        {head}
      </head>

      <body>
        <div class="navbar navbar-inverse navbar-fixed-top">
          <div class="navbar-inner">
            <div class="container">
              <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </a>
              <a class="brand" href="/">Scalatra Examples</a>
              <div class="nav-collapse collapse">

              </div><!--/.nav-collapse -->
            </div>
          </div>
        </div>

        <div class="container">
          <div class="content">
            <div class="page-header">
              <h1>{ title }</h1>
            </div>
            <div class="row">
              <div class="span3">
                <ul class="nav nav-list">
                  <li><a href={url("/cookies-example")}>Cookies example</a></li>
                  <li><a href={url("date/2009/12/26")}>Date example</a></li>
                  <li><a href={url("/upload")}>File upload</a></li>
                  <li><a href={url("/filter-example")}>Filter example</a></li>
                  <li><a href={url("flash-map/form")}>Flash scope</a></li>
                  <li><a href={url("/form")}>Form example</a></li>
                  <li><a href="/">Hello world</a></li>
                </ul>
              </div>
              <div class="span9">
                {content}
              </div>
              <hr/>
            </div>
          </div> <!-- /content -->
        </div> <!-- /container -->
        <footer class="vcard" role="contentinfo">

        </footer>

        <!-- Le javascript
          ================================================== -->
        <!-- Placed at the end of the document so the pages load faster -->
        { (defaultScripts ++ scripts) map { pth =>
        <script type="text/javascript" src={pth}></script>
      } }

      </body>

    </html>
  }
}
