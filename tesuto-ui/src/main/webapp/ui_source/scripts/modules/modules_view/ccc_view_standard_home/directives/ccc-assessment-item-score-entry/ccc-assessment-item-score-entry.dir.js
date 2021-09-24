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

    angular.module('CCC.View.Home').directive('cccAssessmentItemScoreEntry', function () {

        return {

            restrict: 'E',

            scope: {
                item: '='
            },

            controller: [

                '$scope',
                '$element',
                '$compile',

                function ($scope, $element, $compile) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var INVALID_ITEM_DIRECTIVE = 'ccc-assessment-item-score-invalid-item';

                    var interactionTypeMap = {
                        choiceInteraction: 'ccc-assessment-item-score-interaction-choice'
                    };

                    var interactionTargetElement = $element.find('.ccc-assessment-item-score-entry-interaction-target');

                    var generateElementFromDirectiveName = function (directiveName) {
                        return $('<' + directiveName + '></' + directiveName + '>');
                    };

                    var itemElement = $('<div></div>');


                    /*=========== MODEL ===========*/

                    /*=========== LISTENERS ===========*/

                    /*=========== INITIALIZATION ===========*/

                    interactionTargetElement.append(itemElement);

                    var okayToRender = true;
                    _.each($scope.item.responses, function (responseObject) {
                        okayToRender = okayToRender && interactionTypeMap[responseObject.interaction.type];
                    });

                    if (okayToRender) {

                        _.each($scope.item.responses, function (responseObject) {

                            var interactionElement = generateElementFromDirectiveName(interactionTypeMap[responseObject.interaction.type]);
                            interactionElement.attr('response-object','responseObject');

                            var interactionScope = $scope.$new();
                            interactionScope.responseObject = responseObject;

                            interactionElement.appendTo(itemElement);
                            $compile(interactionElement)(interactionScope);
                        });

                    } else {

                        var invalidItemElement = generateElementFromDirectiveName(INVALID_ITEM_DIRECTIVE);
                        itemElement.append(invalidItemElement);
                        $compile(itemElement)($scope);
                    }
                }
            ],

            template: [

                '<div class="ccc-assessment-item-score-entry-index">',
                    '<span class="ccc-assessment-item-score-entry-error" ng-if="item.isInvalid">',
                        '<i class="fa fa-exclamation-triangle" aria-hidden="true"></i><span class="sr-only">Item invalid</span>',
                    '</span>',
                    '<span> {{::item.itemSessionIndex}}.</span>',
                '</div>',
                '<div class="ccc-assessment-item-score-entry-interaction-target"></div>'

            ].join('')

        };

    });

    angular.module('CCC.View.Home').directive('cccAssessmentItemScoreInvalidItem', function () {

        return {

            restrict: 'E',

            template: [

                'Item not compatible with printed assessments.'

            ].join('')

        };
    });

})();
