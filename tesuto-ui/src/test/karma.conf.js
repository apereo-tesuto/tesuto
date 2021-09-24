/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
// Karma configuration
// Generated on Thu Feb 04 2016 16:14:55 GMT-0700 (MST)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
        '../../src/main/webapp/ui/scripts/build/lib.min.js',
        '../../src/main/webapp/ui_lib/angular-1.5.5/angular-mocks.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_components/module.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_components/**/*.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_calculator/module.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_calculator/classes/CalculatorController.cls.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_assessment/module.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_assess/module.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_api/ccc_api_assessments/module.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_asmt_player/module.js',
        '../../src/main/webapp/ui_source/scripts/modules/modules_common/ccc_asmt_player/services/*.js',
        'spec/*.js',
        'spec/**/*.js'
    ],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      '../../src/main/**/*.js':['coverage']
    },

    coverageReporter: {
      type : 'html',
      dir : 'coverage/'
    },

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'coverage'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_DEBUG,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['PhantomJS'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity
  })
}
