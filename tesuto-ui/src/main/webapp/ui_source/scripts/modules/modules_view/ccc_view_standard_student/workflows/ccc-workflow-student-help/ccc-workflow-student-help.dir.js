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

    angular.module('CCC.View.Student').directive('cccWorkflowStudentHelp', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'ViewManagerEntity',

                function ($scope, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addStudentHelpView = function () {

                        var viewScope = $scope.$new();

                        $scope.viewManager.pushView({
                            id: 'student-help',
                            title: 'CCC_STUDENT.CCC-WORKFLOW-STUDENT-HELP.PAGE_TITLES.HELP',
                            breadcrumb: 'CCC_STUDENT.CCC-WORKFLOW-STUDENT-HELP.PAGE_TITLES.HELP',
                            scope: viewScope,
                            template: '<ccc-help is-student="true"></ccc-help>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addStudentHelpView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
