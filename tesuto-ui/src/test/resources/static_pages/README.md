Static Pages
============

Static landing pages as definied in CCCAS-1797.
Scheduled maintenance page definied in CCCAS-3709.

Includes
========

* landing-ci.html
* landing-pilot.html
* landing-test.html
* landing-prod.html
* maintenance.html

Notes
=====

Most CSS is simply a rendered <jsp:include page="/WEB-INF/common/styles.common.jsp" /> with relative paths.

Any updates to static-pages.css needs to be made here first:
tesuto/tesuto-web/src/main/webapp/ui_source/resources/css/static-pages.css

Then run 'grunt build' to copy changes before deploying.