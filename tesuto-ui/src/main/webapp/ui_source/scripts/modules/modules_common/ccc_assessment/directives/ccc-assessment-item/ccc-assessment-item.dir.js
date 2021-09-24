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

    angular.module('CCC.Assessment').directive('cccAssessmentItem', [

        '$compile',

        function ($compile) {

            return {

                restrict: 'E',

                scope: {
                    itemSession: "=",
                    index: "@"
                },

                controller: [

                    '$scope',
                    '$element',
                    'CCC_INTERACTION_ATTR_WHITE_LIST',

                    function ($scope, $element, CCC_INTERACTION_ATTR_WHITE_LIST) {


                        /*============= MODEL  ============*/

                        $scope.isAssessmentItemValid = false;


                        /*============= MODEL DEPENDENT METHODS ============*/

                        /*============= LISTENERS ============*/

                        // check to see if any interaction has validation errors
                        // todo: decide if this is the responsibility of this directive or the assessmentService. Should this property be accessible globally?
                        $scope.$watch(function () {

                            return _.reduce($scope.itemSession.assessmentItem.interactions, function (memo, interaction) {
                                return memo + interaction.validationErrors.length;
                            }, 0) === 0;

                        }, function (isAssessmentItemValid) {

                            $scope.isAssessmentItemValid = isAssessmentItemValid;
                        });

                        // listen for when an interaction has it's value updated so we can bubble up an assessment item event
                        $scope.$on('ccc-interaction.valueCommited', function (e, interactionInfo) {

                            var finalResp = interactionInfo.resp;

                            // here we make a copy of any arrays so that the reference can change and this stays immutable regardless of future changes to assessment item values
                            if (finalResp) {
                                if (angular.isArray(finalResp)) {
                                    finalResp = finalResp.slice(0);
                                }
                            }

                            $scope.$emit('ccc-assessment-item.valueCommited', {
                                type: interactionInfo.type,
                                resp: finalResp,
                                itemSessionId: $scope.itemSession.itemSessionId
                            });
                        });


                        /*============= INITIALIZATION ============*/

                        // here we need to go through and inject interaction directives and compile them all
                        var replacementInteraction;
                        var itemBody = $('<span>' + $scope.itemSession.assessmentItem.itemBody + '</span>');
                        var whiteListAttr;
                        var attrValue;

                        itemBody.find('interaction').each(function (index, interaction) {

                            if ($scope.itemSession.assessmentItem.interactions !== null && $scope.itemSession.assessmentItem.interactions !== undefined && $scope.itemSession.assessmentItem.interactions.length > 0) {
                                $scope.itemSession.assessmentItem.interactions[index].attributes = {};

                                // first get our white listed attributes to pass along to the interaction model
                                for (var i=0; i < CCC_INTERACTION_ATTR_WHITE_LIST.length; i++) {
                                    whiteListAttr = CCC_INTERACTION_ATTR_WHITE_LIST[i];
                                    attrValue = $(interaction).attr(whiteListAttr);
                                    if (attrValue) {
                                        $scope.itemSession.assessmentItem.interactions[index].attributes[whiteListAttr] = attrValue;
                                    }
                                }

                                replacementInteraction = $('<span> </span><ccc-interaction interaction-type="{{::itemSession.assessmentItem.interactions[' + index + '].type}}" index="' + (index + 1) + '" interaction="itemSession.assessmentItem.interactions[' + index + ']"></ccc-interaction><span> </span>');
                                $(interaction).replaceWith(replacementInteraction);
                            }
                        });

                        $element.find('.ccc-assessment-item-body').html(itemBody.html());

                        // if this item has it's own stimulus then inject a stimulus div
                        if ($.trim($scope.itemSession.assessmentItem.stimulus)) {
                            var stimulusMarkup = $('<span></span>').html($scope.itemSession.assessmentItem.stimulus).html();
                            $element.find('.ccc-assessment-item-body').prepend('<div class="ccc-assessment-item-stimulus">' + stimulusMarkup + '</div>');
                        }

                        $compile($element.find('.ccc-assessment-item-body'))($scope);
                    }
                ],

                template: [
                    '<div class="ccc-assessment-item-inner" ng-class="{\'assessment-item-invalid\':!isAssessmentItemValid}">',
                        '<span class="ccc-assessment-item-number">',
                            '<span class="ccc-assessment-item-number-warning"><i class="fa fa-exclamation-triangle"></i><span class="sr-only" translate="CCC_ASSESSMENT.ASSESSMENT_ITEM.SR_INVALID_WARNING"></span></span>',
                            '<ccc-question-label tabindex="-1" class="ccc-hover" text="index"></ccc-question-label>',
                        '</span>',
                        '<ccc-assessment-item-session-feedback ng-if="itemSession.feedback && itemSession.feedback.length" item-session="itemSession" class="alert alert-info"></ccc-assessment-item-session-feedback>',
                        '<div>',
                            '<div class="ccc-assessment-item-body"></div>',
                        '</div>',
                    '</div>'
                ].join('')

            };
        }
    ]);

    /*============= FEEDBACK FOR AN ASSESSMENT ITEM ( USED RIGHT NOW FOR PREVIEW SCORING ) =============*/

    angular.module('CCC.Assessment').directive('cccAssessmentItemSessionFeedback', [

        function () {

            return {

                restrict: 'E',

                scope: {
                    itemSession: "="
                },

                template: [
                    '<div class="ccc-assessment-item-session-feedback-item" ng-repeat="feedback in itemSession.feedback">',
                        '<ccc-assessment-item-session-feedback-item feedback="feedback"></ccc-assessment-item-session-feedback-item>',
                    '</div>'
                ].join('')

            };
        }
    ]);

    angular.module('CCC.Assessment').directive('cccAssessmentItemSessionFeedbackItem', [

        function () {

            return {

                restrict: 'E',

                scope: {
                    feedback: "="
                },

                controller: [

                    '$scope',
                    '$element',
                    '$compile',

                    function ($scope, $element, $compile) {


                        /*============= MODEL  ============*/

                        /*============= MODEL DEPENDENT METHODS ============*/

                        /*============= LISTENERS ============*/

                        /*============= INITIALIZATION ============*/

                        var feedbackElement = $($scope.feedback.template);
                        feedbackElement.appendTo($element);

                        $compile(feedbackElement)($scope);
                    }
                ]
            };

        }
    ]);

    angular.module('CCC.Assessment').directive('cccAssessmentItemSessionFeedbackOutcome', [

        function () {

            return {

                restrict: 'E',

                scope: {
                    feedback: "="
                },

                template: [
                    '<div class="row">',
                        '<div class="col-xs-12">',
                            '<i class="fa fa-line-chart"></i> OUTCOME: ',
                            '<span class="ccc-assessment-item-session-feedback-outcome-value">',
                                'MIN = <span class="ccc-assessment-item-session-feedback-min-value">{{feedback.data.min ? feedback.data.min : \'NULL\'}}</span> / ',
                                'MAX = <span class="ccc-assessment-item-session-feedback-max-value">{{feedback.data.max ? feedback.data.max : \'NULL\'}}</span> / ',
                                'RAW SCORE = <span class="ccc-assessment-item-session-feedback-raw-value">{{::feedback.data.rawScore}}</span> / ',
                                'FINAL SCORE = <span class="ccc-assessment-item-session-feedback-final-value">{{::feedback.data.score}}</span> ',
                            '</span>',
                        '</div>',
                    '</div>'
                ].join('')
            };

        }
    ]);

    angular.module('CCC.Assessment').directive('cccAssessmentItemSessionFeedbackScore', [

        function () {

            return {

                restrict: 'E',

                scope: {
                    feedback: "="
                },

                template: [
                    '<div class="row">',
                        '<div class="col-xs-12">',
                            '<i class="fa fa-circle"></i> SCORE = ',
                            '<span class="ccc-assessment-item-session-feedback-score-value">{{::feedback.data.score}}</span>',
                            '{{::feedback.data.interactionType}} / {{::feedback.data.responseIdentifier}}',
                        '</div>',
                    '</div>'
                ].join('')
            };

        }
    ]);

})();
