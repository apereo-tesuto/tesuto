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

    // https://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#section10092

    angular.module('CCC.AsmtPlayer').directive('cccAsmtPlayerToolbarBottom', function () {

        return {

            restrict: 'E',

            scope: {
                allowPause: '=',
                assessment: "=",
            },

            controller: [

                '$rootScope',
                '$scope',
                '$element',
                'AsmtService',

                function ($rootScope, $scope, $element, AsmtService) {


                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var focusNext = function () {
                        $element.find('.ccc-asmt-next-button').focus();
                    };

                    var focusPrevious = function () {
                        $element.find('.ccc-asmt-previous-button').focus();
                    };


                    /*============ MODEL ===========*/

                    // INHERIT: attach the state properties from AsmtService
                    // interactionsList
                    // isAssessmentItemValid
                    // interactionsComplete
                    // interactionsCount
                    $scope.asmtState = AsmtService.state;

                    $scope.assessment = AsmtService.assessment;

                    $scope.allowPause = $scope.allowPause || false;

                    $scope.isScoreDisabled = AsmtService.isScoreDisabled();

                    // don't show the parts summary for now ( requirements shifting )
                    $scope.showPartsSummary = false;

                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.nextAssessmentTask = function () {
                        $scope.$emit('ccc-asmt-player-toolbar-bottom.nextClicked');
                    };

                    $scope.toggleScoreTaskSet = function () {
                        $scope.$emit('ccc-asmt-player-toolbar-bottom.toggleShowScores');
                    };

                    $scope.previousAssessmentTask = function () {
                        $scope.$emit('ccc-asmt-player-toolbar-bottom.prevClicked');
                    };

                    $scope.pauseAssessment = function () {
                        $scope.$emit('ccc-asmt-player-toolbar-bottom.pauseClicked');
                    };


                    /*============ LISTENERS ===========*/

                    // parents can ask us to focus on certain buttons
                    $scope.$on('ccc-asmt-player-toolbar-bottom.requestFocusNext', focusNext);
                    $scope.$on('ccc-asmt-player-toolbar-bottom.requestFocusPrevious', focusPrevious);


                    /*============ INITIALIZATION ===========*/

                }
            ],

            template: [
                '<div class="container">',
                    '<div class="row">',

                        '<div class="col-sm-6 col-sm-push-3 col-xs-9 sm-text-center xs-text-left">',
                            '<div class="ccc-asmt-btn-group">',

                                '<button ng-if="assessment.currentTaskSet.navigationMode === \'NONLINEAR\' && asmtState.totalTaskSetIndex !== 1" class="btn btn-lg btn-primary ccc-asmt-previous-button" role="button" ng-class="previousButtonClass()" ng-disabled="asmtState.totalTaskIndex === 0 || asmtState.isNavigationDisabled" ng-click="previousAssessmentTask()" tabindex="0">',
                                    '<i class="fa fa-chevron-left" aria-hidden="true"></i> <span translate="CCC_PLAYER.CONTROLS.PREV.BUTTON"></span>',
                                '</button>',
                                '<button class="btn btn-lg btn-primary ccc-asmt-next-button" ng-click="nextAssessmentTask()" ng-disabled="asmtState.isNavigationDisabled">',
                                    '<span translate="CCC_PLAYER.CONTROLS.NEXT.BUTTON"></span>',
                                    ' <i class="fa fa-chevron-right" aria-hidden="true"></i>',
                                '</button>',

                            '</div>',
                        '</div>',

                        '<div class="col-xs-3 col-sm-push-3 text-right">',

                            '<button class="btn btn-lg btn-default btn-has-responsive-label ccc-asmt-pause-button" ng-click="pauseAssessment()" ng-if="allowPause">',
                                '<i class="fa fa-pause" aria-hidden="true"></i> ',
                                '<span translate="CCC_PLAYER.CONTROLS.PAUSE.BUTTON"></span>',
                            '</button>',

                            '<button class="btn btn-lg btn-default ccc-asmt-score-button" ng-click="toggleScoreTaskSet()" ng-if="!isScoreDisabled" ng-class="{active: asmtState.showScoresActive}" ng-disabled="isScoreDisabled" tabindex="0">',
                                '<i class="fa fa-line-chart noanim" ng-if="!asmtState.isLoadingScores"></i> ',
                                '<i class="fa fa-spinner fa-spin noanim" ng-if="asmtState.isLoadingScores"></i> ',
                                '<span translate="CCC_PLAYER.CONTROLS.SCORE.BUTTON"></span>',
                            '</button>',

                        '</div>',

                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();
