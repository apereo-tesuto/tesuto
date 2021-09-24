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

    angular.module('CCC.AsmtPlayer').directive('cccAsmtPlayerContent', function () {

        return {

            restrict: 'E',

            scope: {
                assessment: '='
            },

            controller: [

                '$scope',
                '$element',
                '$compile',
                'AssessmentTaskLayoutService',
                'MathService',

                function ($scope, $element, $compile, AssessmentTaskLayoutService, MathService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    var $childScope = null;
                    var taskTargetContainer = $element.find('.ccc-asmt-player-task');

                    var redrawMath = function () {
                        MathService.rerender();
                    };


                    /*=========== MODEL DEPENDENT METHODS ============*/

                    var loadAssessmentTask = function () {

                        //============ RUN PRE PROCESSING STEPS

                        // hide all math while it renders
                        $('body').removeClass('show-math');


                        //============ EMPTY THE AREA AND CREATE NEW CHILD SCOPE

                        // allow for destroy before clearing the area
                        if ($childScope) {
                            $childScope.$destroy();
                        }

                        // we need a fresh child scope
                        $childScope = $scope.$new();

                        // empty the area
                        taskTargetContainer.empty();

                        $childScope.assessmentTask = $scope.assessmentTask;


                        //============ RENDER THE LAYOUT

                        var layoutData = AssessmentTaskLayoutService.getLayoutForAssessmentTask($scope.assessmentTask);

                        var assessmentTaskNode = angular.element(layoutData.template);
                        taskTargetContainer.append(assessmentTaskNode);

                        // here we create a new form with the name "assessmentTaskForm" for the assessmentTask
                        var itemsTarget = $('<div ng-form name="assessmentTaskForm"></div>');
                        itemsTarget.appendTo(assessmentTaskNode.find('.layout-container-items'));


                        //============ RENDER THE STIMULUS

                        var stimulusTarget = assessmentTaskNode.find('.layout-container-stimulus');
                        var stimulusNode = angular.element($childScope.assessmentTask.stimulus);

                        stimulusTarget.append(stimulusNode);


                        //============ COMPILE THE ENTIRE ASSESSMENT ITEM

                        $compile(assessmentTaskNode)($childScope);


                        //============ RENDER THE ITEMS

                        var itemNode;
                        var $itemScope;

                        for (var i=0; i < $childScope.assessmentTask.itemSessions.length; i++) {

                            $itemScope = $childScope.$new();
                            $itemScope.itemSession = $childScope.assessmentTask.itemSessions[i];

                            itemNode = $('<ccc-assessment-item item-session="itemSession" index="' + $itemScope.itemSession.itemSessionIndex + '"></ccc-assessment-item>');
                            itemsTarget.append(itemNode);
                            $compile(itemNode)($itemScope);
                        }


                        //============ RUN POST PROCESSING STEPS

                        // typeset all MathML for this assessment item
                        setTimeout(function () {

                            MathService.render(function () {
                                $scope.$broadcast('CCC.assessment-player.ready');
                                $scope.$apply();
                            });

                            // focus on the top of the content area
                            $element.find('.ccc-asmt-player-content-area').focus();
                            $element.scrollTop(0);
                        });
                    };


                    /*=========== LISTENERS ===========*/

                    // watch for resizes to redraw mathematics
                    $scope.$on('CCC.window.resize', redrawMath);

                    // watch for id changes on the item to load a new item
                    $scope.$on('ccc-asmt-player-content.requestSetTask', function (event, assessmentTask) {

                        if (assessmentTask) {
                            $scope.assessmentTask = assessmentTask;
                            loadAssessmentTask();
                        }
                    });

                    // watch for requests to focus on the first invalid item
                    $scope.$on('ccc-asmt-player.itemsInvalid', function () {
                        $($element.find('input.ng-invalid, textarea.ng-invalid, .invalid-error-message, button.ng-invalid').not(".hidden")[0]).focus();
                    });


                    // watch the status of the current form that was created while loading a new item and bubble up its status
                    $scope.$watch(function () {
                        return $childScope && $childScope.assessmentTaskForm.$pristine;
                    }, function (isPristine) {
                        $scope.$emit('ccc-asmt-player-content.pristineChanged', isPristine);
                    });

                    $scope.$watch(function () {
                        return $childScope && $childScope.assessmentTaskForm.$valid;
                    }, function (isValid) {
                        $scope.$emit('ccc-asmt-player-content.validityChanged', isValid);
                    });


                    /*=========== INITIALIZATION ===========*/

                }
            ],

            template: [
                '<main role="main" class="ccc-asmt-player-content-container">',
                    '<div class="container">',

                        '<div class="ccc-asmt-player-content-area" tabindex="0">',

                            '<div class="row">',
                                '<div class="col-xs-12">',

                                    '<div class="ccc-asmt-player-task" ccc-lang="{{assessment.language}}"></div>',

                                '</div>',
                            '</div>',

                        '</div>',

                    '</div>',
                '</main>'
            ].join('')

        };

    });

})();
