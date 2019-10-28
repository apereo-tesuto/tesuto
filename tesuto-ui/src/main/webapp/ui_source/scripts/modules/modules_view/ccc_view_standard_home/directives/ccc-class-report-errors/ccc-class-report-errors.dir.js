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
(function () {

    angular.module('CCC.View.Home').directive('cccClassReportErrors', function () {

        return {

            restrict: 'E',

            scope: {
                errors: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var errorCheckMap = {
                        'CCCID_EMPTY': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.CCCID_EMPTY.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.CCCID_EMPTY.BODY'
                        },
                        'CCCID_HEADER_MISSING': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.CCCID_HEADER_MISSING.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.CCCID_HEADER_MISSING.BODY'
                        },
                        'DUPLICATE_CCCID': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.DUPLICATE_CCCID.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.DUPLICATE_CCCID.BODY'
                        },
                        'FILE_DOESNT_EXIST': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.FILE_DOESNT_EXIST.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.FILE_DOESNT_EXIST.BODY'
                        },
                        'FIRST_NAME_EMPTY': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.FIRST_NAME_EMPTY.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.FIRST_NAME_EMPTY.BODY'
                        },
                        'INVALID_CCCID_LENGTH': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.INVALID_CCCID_LENGTH.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.INVALID_CCCID_LENGTH.BODY'
                        },
                        'INVALID_STUDENT': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.INVALID_STUDENT.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.INVALID_STUDENT.BODY'
                        },
                        'JSON_MAPPING_ERROR': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.JSON_MAPPING_ERROR.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.JSON_MAPPING_ERROR.BODY'
                        },
                        'LAST_NAME_EMPTY': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.LAST_NAME_EMPTY.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.LAST_NAME_EMPTY.BODY'
                        },
                        'MINIMUM_ASSESSED_STUDENTS_NOT_MET': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MINIMUM_ASSESSED_STUDENTS_NOT_MET.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MINIMUM_ASSESSED_STUDENTS_NOT_MET.BODY'
                        },
                        'MINIMUM_SCORED_STUDENTS_NOT_MET': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MINIMUM_SCORED_STUDENTS_NOT_MET.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MINIMUM_SCORED_STUDENTS_NOT_MET.BODY'
                        },'MINIMUM_STUDENTS_NOT_MET': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MINIMUM_STUDENTS_NOT_MET.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MINIMUM_STUDENTS_NOT_MET.BODY'
                        },
                        'MISSING_ASSESSMENT_METADATA': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MISSING_ASSESSMENT_METADATA.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.MISSING_ASSESSMENT_METADATA.BODY'
                        },
                        'NAME_MISMATCH': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.NAME_MISMATCH.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.NAME_MISMATCH.BODY'
                        },
                        'PARSING_FAILED': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.PARSING_FAILED.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.PARSING_FAILED.BODY'
                        },
                        'UPLOAD_FAILED': {
                            title: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.UPLOAD_FAILED.TITLE',
                            body: 'CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.ERRORS.UPLOAD_FAILED.BODY'
                        }
                    };


                    /*============ MODEL ==============*/

                    var parseErrors = function (errorList) {

                        _.each(errorList, function (errors) {

                            _.each(errors.errors, function (error) {

                                var map = errorCheckMap[error.errorCode] ? errorCheckMap[error.errorCode] : {};

                                errors.title = map.title ? map.title : null;
                                errors.body = map.body ? map.body : null;
                            });
                        });
                        console.log(errorList);
                    };


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============= BEHAVIOR =============*/

                    $scope.tryAgain = function () {
                        $scope.$emit('ccc-class-report-errors.tryAgain');
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    parseErrors($scope.errors);

                }
            ],

            template: [

                '<div class="form-container ccc-form-container">',
                    '<div class="form-body">',
                        '<div class="alert alert-warning alert-with-icon large">',
                            '<span class="icon fa fa-warning" role="presentation" aria-hidden="true"></span>',
                            '<div class="alert-body">',
                                '<h2 class="title" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.TITLE"></h2>',
                                '<p translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.SUBHEAD"></p>',
                            '</div>',
                        '</div>',
                        '<div class="row">',
                            '<div class="col-md-8 col-md-offset-1">',
                                '<div ng-repeat="error in errors track by $index" class="errors">',
                                    '<h3 class="title"><span class="icon fa fa-times-circle text-danger" role="presentation" aria-hidden="true"></span> <span translate="{{error.title}}"></span></h3>',
                                    '<p translate="{{error.body}}"></p>',
                                    '<p ng-if="error.lineNumber && error.lineNumber !== -1" class="line-number"><strong>Line {{error.lineNumber}}</strong></p>',
                                    '<div ng-repeat="details in error.errors track by $index">',
                                        '<pre ng-if="details.columnName || details.columnValue">',
                                            '<span ng-if="details.columnName">{{details.columnName}}: </span>',
                                            '<span ng-if="details.columnValue">{{details.columnValue}}</span>',
                                        '</pre>',
                                        '<div ng-if="details.errorMessageArgs && details.errorMessageArgs[0] !== null">',
                                            '<pre>',
                                                '<div ng-repeat="arg in details.errorMessageArgs track by $index">',
                                                    '<span>{{arg}}</span>',
                                                    '<span ng-if="details.errorCode === \'MINIMUM_STUDENTS_NOT_MET\' && $index === 0"> provided</span>',
                                                    '<span ng-if="details.errorCode === \'MINIMUM_STUDENTS_NOT_MET\' && $index === 1"> required</span>',
                                                    '<span ng-if="details.errorCode === \'MINIMUM_ASSESSED_STUDENTS_NOT_MET\' && $index === 0"> assessed</span>',
                                                    '<span ng-if="details.errorCode === \'MINIMUM_ASSESSED_STUDENTS_NOT_MET\' && $index === 1"> required</span>',
                                                    '<span ng-if="details.errorCode === \'MINIMUM_SCORED_STUDENTS_NOT_MET\' && $index === 0"> scored</span>',
                                                    '<span ng-if="details.errorCode === \'MINIMUM_SCORED_STUDENTS_NOT_MET\' && $index === 1"> required</span>',
                                                '</div>',
                                            '</pre>',
                                        '</div>',
                                        '<p>',
                                            '<span class="tag label label-danger">{{details.errorCode}}</span>',
                                        '</p>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                    '<div class="form-actions">',
                        '<div class="row">',
                            '<div class="col-md-6 col-md-offset-3">',
                                '<div class="actions">',
                                    '<button class="btn btn-primary" ng-click="tryAgain()"><span class="icon fa fa-chevron-left"></span> <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.TRY_AGAIN"></span></button>',
                                    '<button class="btn btn-default" ui-sref="home"><span class="icon fa fa-times" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-ERRORS.CANCEL"></span></button>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
