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

    angular.module('CCC.View.Home').directive('cccWorkflowClassReport', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                'ViewManagerEntity',

                function ($scope, ViewManagerEntity) {


                    /*============ PRIVATE VARIABLES =============*/

                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ MODEL DEPENDENT VIEW MANAGMENT ============*/

                    var addClassReportView = function (report) {

                        var viewScope = $scope.$new();
                        viewScope.report = report;

                        $scope.viewManager.pushView({
                            id: 'class-report-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-CLASS-REPORT.RESULTS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-CLASS-REPORT.RESULTS',
                            scope: viewScope,
                            template: '<ccc-class-report report="report"></ccc-class-report>'
                        });
                    };

                    var addClassReportErrorView = function (errors) {

                        var viewScope = $scope.$new();
                        viewScope.errors = errors;

                        viewScope.$on('ccc-class-report-errors.tryAgain', function () {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'class-report-error-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-CLASS-REPORT.RESULTS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-CLASS-REPORT.RESULTS',
                            scope: viewScope,
                            template: '<ccc-class-report-errors errors="errors"></ccc-class-report-errors>'
                        });
                    };

                    var addClassReportSetupView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-class-report-setup.generateErrors', function (e, errors) {
                            addClassReportErrorView(errors);
                        });

                        viewScope.$on('ccc-class-report-setup.generateSuccess', function (e, report) {
                            addClassReportView(report);
                        });

                        $scope.viewManager.pushView({
                            id: 'class-report-setup-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-CLASS-REPORT.CLASS_REPORT',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-CLASS-REPORT.CLASS_REPORT',
                            scope: viewScope,
                            template: '<ccc-class-report-setup></ccc-class-report-setup>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addClassReportSetupView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
