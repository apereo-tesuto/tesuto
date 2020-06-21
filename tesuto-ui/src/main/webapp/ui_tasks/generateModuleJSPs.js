module.exports = function (grunt) {
    'use strict';

    // from nodeJS
    var fs = require('fs');
    var path = require('path');

    // 3rd party
    var _ = require('underscore');

    // ensure the dynamic directory is created
    // TODO, make this dynamic so we can just create directories as needed
    var dir = grunt.option('envConfigs').dynamicJSPTarget;

    if (!fs.existsSync(dir)){
         fs.mkdirSync(dir);
    }

    // because lib files are not in the same relative location as script files we need to transform the path here for the final jsp statments
    var cleanLibraryPath = function (path) {
        return path.replace('../resources/static/', '');
    };

    // CCC Assess Specific
    var javascriptResourceConfigs = require('../grunt.javascript.js')(grunt);

    grunt.registerTask('generateModuleJSPs', 'Generate JSPs for JavaScript Modules', function () {

        var scriptsBasePath = 'ui/';

        // for each javascript group, look at it's sources and minified target and generate the appropriate jsp
        _.each(javascriptResourceConfigs.groups, function (javaScriptGroup) {

            // this jsp filepath should be relative to src/main/webapp/ (where the Gruntfile.js file is)
            var targetFile = javaScriptGroup.targetJsp;
            var targetFileString = '<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>\n';


            /*=========== CONDITIONAL ON MINIFIED FILES ============*/

            targetFileString = targetFileString + '';

            // conditionalStart for use source
            targetFileString = targetFileString + '<c:if test=\'${pageContext.request.getParameter("usesource")!="true"}\'>\n';

                // add in the library files
                _.each(javaScriptGroup.libraries, function (javaScriptSource) {
                    targetFileString = targetFileString + '    <script type="text/javascript" src="' + cleanLibraryPath(scriptsBasePath + javaScriptSource) + '"></script>\n';
                });

                // the minified file
                targetFileString = targetFileString + '    <script type="text/javascript" src="' + scriptsBasePath + javaScriptGroup.targetMinScript + '"></script>\n';

            // conditionalEnd for use minified
            targetFileString = targetFileString + '</c:if>\n';


            /*=========== CONDITIONAL SOURCE FILES ============*/

            // conditionalStart for use source
            targetFileString = targetFileString + '<c:if test=\'${pageContext.request.getParameter("usesource")=="true"}\'>\n';

                // add in the library files
                _.each(javaScriptGroup.libraries, function (javaScriptSource) {
                    targetFileString = targetFileString + '    <script type="text/javascript" src="' + cleanLibraryPath(scriptsBasePath + javaScriptSource) + '"></script>\n';
                });

                // add in the source files
                _.each(javaScriptGroup.sources, function (javaScriptSource) {

                    if (javaScriptGroup.isLibraryGroup) {
                        targetFileString = targetFileString + '    <script type="text/javascript" src="' + cleanLibraryPath(scriptsBasePath + javaScriptSource) + '"></script>\n';
                    } else {
                        targetFileString = targetFileString + '    <script type="text/javascript" src="' + scriptsBasePath + javaScriptSource + '"></script>\n';
                    }
                });

            // conditionalEnd for use minified
            targetFileString = targetFileString + '</c:if>\n\n';


            /*=========== WRITE THE FILE ============*/

            fs.writeFileSync(targetFile, targetFileString, 'utf8');
        });

    });
};