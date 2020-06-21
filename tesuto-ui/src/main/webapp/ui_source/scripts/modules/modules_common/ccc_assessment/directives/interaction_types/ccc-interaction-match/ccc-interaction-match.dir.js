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

    /**
     * QTI Specs: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10296
     * XML Example: http://www.imsglobal.org/question/qtiv2p1/examples/items/match.xml
     */

    angular.module('CCC.Assessment').directive('cccInteractionMatch', function () {

        return {

            restrict: 'E',

            scope: {
                interaction: "="
            },

            controller: [

                '$scope',
                '$element',
                '$translate',

                function ($scope, $element, $translate) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    // currently we are not using focused cell behavior, keep all focused/highlight cell logic here until we get this interaction fully tested
                    // changes in decisions around implementation may warrant using this again
                    var currentFocusedCell = {
                        row: false,
                        col: false
                    };

                    var unhighlightAllCells = function () {
                        $element.find('.ccc-interaction-match-highlight').removeClass('ccc-interaction-match-highlight');
                    };

                    var highlightCell = function (cell) {
                        unhighlightAllCells();
                        $($($element.find('tbody')[cell.row]).find('td')[0]).addClass('ccc-interaction-match-highlight');
                        $($element.find('thead th')[cell.col + 1]).addClass('ccc-interaction-match-highlight');
                    };

                    // the match interaction must use objects to track values for validation purposes
                    // so we populate initial values using valueObject creation.. the directive will work on converting the
                    // value objects in the the standard interaction values array
                    var getExistingValuesMap = function () {

                        var existingValuesMap = {};

                        if ($scope.interaction) {

                            // matchStringValue will look like "A B" where A is the source identifier and B is the target identifier
                            _.each($scope.interaction.values, function (matchStringValue) {

                                var sourceId = matchStringValue.split(' ')[0];
                                var targetId = matchStringValue.split(' ')[1];

                                existingValuesMap[sourceId] = existingValuesMap[sourceId] || {};
                                existingValuesMap[sourceId][targetId] = true;
                            });

                        }

                        return existingValuesMap;
                    };


                    /*============ MODEL =============*/

                    // this will be the main model used in the UI for the input bindings
                    $scope.matchModel = [];

                    // our model to track validation match errors
                    $scope.matchCountMap = {};
                    $scope.interaction.matchMaxValidationErrors = [];
                    $scope.interaction.matchMinValidationErrors = [];
                    $scope.showMatchCountValidationErrors = false;

                    // used for rendering but not for values
                    $scope.sourceSet = $scope.interaction.sourceMatchSets;
                    $scope.targetSet = $scope.interaction.targetMatchSets;


                    /*============ MODEL DEPENDENT METHODS =============*/

                    // building a nested array data structure to double as the model for rendering the template as well as
                    // the model for the input values, this will need to be scanned to gather the values
                    var populateMatchModel = function () {

                        // first build a source and target mapping of incoming values
                        var existingValuesMap = getExistingValuesMap();
                        var getIsSourceTargetSelected = function (sourceId, targetId) {

                            if (existingValuesMap[sourceId]) {
                                return existingValuesMap[sourceId][targetId] ? true : false;
                            } else {
                                return false;
                            }
                        };

                        _.each($scope.sourceSet, function (sourceSimpleAssociableChoice) {

                            var sourceObject = {
                                identifier: sourceSimpleAssociableChoice.identifier,
                                content: sourceSimpleAssociableChoice.content,
                                trustedContent: sourceSimpleAssociableChoice.trustedContent,
                                matchMin: sourceSimpleAssociableChoice.matchMin,
                                matchMax: sourceSimpleAssociableChoice.matchMax,
                                matches: [],
                                errors: []
                            };

                            _.each($scope.targetSet, function (targetSimpleAssociableChoice) {

                                // notice we make copies of all the primitives here in a new object
                                // this is intentional, we want to copy the target objects form QTI once for each sourceObject
                                sourceObject.matches.push({
                                    identifier: targetSimpleAssociableChoice.identifier,
                                    content: targetSimpleAssociableChoice.content,
                                    trustedContent: targetSimpleAssociableChoice.trustedContent,
                                    matchMin: targetSimpleAssociableChoice.matchMin,
                                    matchMax: targetSimpleAssociableChoice.matchMax,
                                    errors: [],
                                    value: getIsSourceTargetSelected(sourceObject.identifier, targetSimpleAssociableChoice.identifier)
                                });
                            });

                            $scope.matchModel.push(sourceObject);
                        });
                    };

                    var getNewMaxError = function (key, content, max) {

                        var maxError = {
                            live: true, // we want to see these even before submission
                            error: key + content + ':CCC_ASSESSMENT.INTERACTION.MATCH.VALIDATION.ASSOCIABLE_MAX',
                            msg: ''
                        };

                        $translate('CCC_ASSESSMENT.INTERACTION.MATCH.VALIDATION.ASSOCIABLE_MAX', {
                            CONTENT: content,
                            MAX: max
                        }, 'messageformat').then(function (translation) {
                            maxError.msg = '<strong>' + content + ':</strong> ' + translation;
                        });

                        return maxError;
                    };

                    var getNewMinError = function (key, content, min) {

                        var minError = {
                            error: key + content + ':CCC_ASSESSMENT.INTERACTION.MATCH.VALIDATION.ASSOCIABLE_MIN',
                            msg: ''
                        };

                        $translate('CCC_ASSESSMENT.INTERACTION.MATCH.VALIDATION.ASSOCIABLE_MIN', {
                            CONTENT: content,
                            MIN: min
                        }, 'messageformat').then(function (translation) {
                            minError.msg = '<strong>' + content + ':</strong> ' + translation;
                        });

                        return minError;
                    };

                    var runMatchValidations = function (matchCountMap) {

                        $scope.interaction.matchMaxValidationErrors = [];
                        $scope.interaction.matchMinValidationErrors = [];

                        for (var key in matchCountMap) {
                            if (matchCountMap.hasOwnProperty(key)) {

                                var thisMatchCount = matchCountMap[key];
                                thisMatchCount.errors = [];

                                if (thisMatchCount.matchMax !== 0 && thisMatchCount.matchCount > thisMatchCount.matchMax) {

                                    // so we always want the official interaction errors to be correct
                                    $scope.interaction.matchMaxValidationErrors.push(getNewMaxError(key, thisMatchCount.associable.content, thisMatchCount.matchMax));
                                    thisMatchCount.errors.push(getNewMaxError(key, thisMatchCount.associable.content, thisMatchCount.matchMax));

                                    thisMatchCount.matchMaxInvalid = true;
                                    thisMatchCount.invalid = true;
                                }

                                if (thisMatchCount.matchMin !== 0 && thisMatchCount.matchCount < thisMatchCount.matchMin) {

                                    // so we always want the official interaction errors to be correct
                                    $scope.interaction.matchMinValidationErrors.push(getNewMinError(key, thisMatchCount.associable.content, thisMatchCount.matchMin));
                                    thisMatchCount.errors.push(getNewMinError(key, thisMatchCount.associable.content, thisMatchCount.matchMin));

                                    thisMatchCount.matchMinInvalid = true;
                                    // ...but our own internal model is used for display and behavior so we integrate different logic for validation
                                    // thisMatchCount.invalid = true;
                                }
                            }
                        }
                    };

                    var updateInteractionValues = function () {

                        // reset our model that is used to track validation errors and messages
                        $scope.matchCountMap = {};

                        $scope.interaction.values = [];
                        $scope.interaction.valueObjects = [];

                        // loop through our model to pull out values
                        _.each($scope.matchModel, function (sourceObject) {

                            $scope.matchCountMap[sourceObject.identifier] = {
                                associable: sourceObject,
                                matchMax: sourceObject.matchMax,
                                matchMin: sourceObject.matchMin,
                                validations: [],
                                matchMaxInvalid: false,
                                matchMinInvalid: false,
                                invalid: false,
                                matchCount: 0
                            };

                            _.each(sourceObject.matches, function (targetObject) {

                                if (!$scope.matchCountMap[targetObject.identifier]) {
                                    $scope.matchCountMap[targetObject.identifier] = {
                                        associable: targetObject,
                                        matchMax: targetObject.matchMax,
                                        matchMin: targetObject.matchMin,
                                        validations: [],
                                        matchCount: 0
                                    };
                                }

                                // check the values
                                if (targetObject.value) {

                                    $scope.matchCountMap[sourceObject.identifier].matchCount++;
                                    $scope.matchCountMap[targetObject.identifier].matchCount++;

                                    // add a sourc/target object to valueObjects that will later be turned into string values
                                    $scope.interaction.valueObjects.push({
                                        source: sourceObject.identifier,
                                        target: targetObject.identifier
                                    });
                                }
                            });
                        });

                        $scope.interaction.values = _.map($scope.interaction.valueObjects, function (valueObject) {
                            return valueObject.source + ' ' + valueObject.target;
                        });

                        runMatchValidations($scope.matchCountMap);

                        // and return a unique string so watchers can determine if any values have changed
                        return $scope.interaction.values.join('');
                    };

                    var itemsUpdated = function () {

                        // CONTRACT: let everyone know the value model has changed
                        $scope.$emit('ccc-interaction.updated');

                        // CONTRACT: every interaction needs to notify when a user commits a change to the values
                        $scope.$emit('ccc-interaction.valueCommited', {
                            type:'match',
                            resp: $scope.interaction.values
                        });
                    };

                    var valuesHaveChanged = function () {
                        updateInteractionValues();
                    };


                    /*============ BEHAVIOR =============*/

                    $scope.cellFocused = function (cellRow, cellColumn) {

                        if (currentFocusedCell.row !== cellRow || currentFocusedCell.col !== cellColumn) {
                            currentFocusedCell.row = cellRow;
                            currentFocusedCell.col = cellColumn;
                        }
                        highlightCell(currentFocusedCell);
                    };

                    $scope.unhighlightTable = function () {
                        unhighlightAllCells();
                    };

                    $scope.getInputAriaLabel = function (sourceIdentifier, targetIdentifier) {

                        var matchIdentifierString = $scope.matchCountMap[sourceIdentifier].associable.content + ' ' + $scope.matchCountMap[targetIdentifier].associable.content;

                        // if we are asked to hide all internal validation then we just return the normal identifier
                        if ($scope.interaction.hideInternalResponseValidationErrors) {

                            return matchIdentifierString;

                        } else {

                            var sourceErrorMessages = _.map($scope.matchCountMap[sourceIdentifier].errors, function (error) {
                                return $('<span>' + error.msg + '</span>').text();
                            });
                            var targetErrorMessages = _.map($scope.matchCountMap[targetIdentifier].errors, function (error) {
                                return $('<span>' + error.msg + '</span>').text();
                            });

                            return matchIdentifierString + ', ' + sourceErrorMessages.join(',') + targetErrorMessages.join(',');
                        }
                    };

                    // this allows the user to click the entire cell around the checkbox
                    $scope.toggleValue = function (event, target) {

                        // shift focus to the button so things don't look goofy
                        $(event.target).find('button').focus();

                        // not allowed via keyboard, only by mouse
                        if (event.type !== 'keypress') {
                            target.value = !target.value;
                        }
                    };


                    /*============ LISTENERS =============*/

                    // we just constantly watch the values during each digest and call itemsUpdated if any values change
                    $scope.$watch(function () {

                        return $scope.interaction.values.join('');

                    }, function (newValue, oldValue) {

                        // prevent the initial firing because nothing has changed yet
                        if (newValue !== oldValue) {
                            itemsUpdated();
                        }
                    });

                    $scope.$on('ccc-toggle-input.changed', valuesHaveChanged);


                    /*============ INITIALIZATION =============*/

                    populateMatchModel();
                    valuesHaveChanged();
                }
            ],

            template: [
                '<span ng-form class="ccc-interaction-form" data-uuid="{{interaction.uiid}}">',

                    '<label ng-bind-html="interaction.prompt" class="ccc-interaction-prompt ccc-fg" id="interaction-{{interaction.uiid}}"></label>',

                    '<div class="ccc-interaction-match-container">',
                        '<table class="table-bordered table-hover" ng-mouseout="unhighlightTable()" aria-labelledby="interaction-{{interaction.uiid}}">',
                            '<thead>',
                                '<tr>',
                                    '<th ng-mouseover="unhighlightTable()"></th>',
                                    '<th scope="col" ng-repeat="target in targetSet" id="{{::interaction.uiid}}-{{::target.identifier}}" ng-class="{\'ccc-interaction-match-has-max-error\': matchCountMap[target.identifier].matchMaxInvalid, \'ccc-interaction-match-has-min-error\': matchCountMap[target.identifier].matchMinInvalid}">',
                                        '<i class="fa fa-exclamation-triangle ccc-interaction-match-error-icon" ng-if="!interaction.hideInternalResponseValidationErrors" aria-hidden="true"></i>',
                                        '<span class="ccc-cell-header ccc-fg" ng-bind-html="target.trustedContent"></span>',
                                    '</th>',
                                '</tr>',
                            '</thead>',
                            '<tbody ng-repeat="source in matchModel">',
                                '<tr>',
                                    '<td class="ccc-interaction-match-row-header" scope="row" id="{{::interaction.uiid}}-{{::source.identifier}}" ng-mouseover="unhighlightTable()" ng-class="{\'ccc-interaction-match-has-max-error\': matchCountMap[source.identifier].matchMaxInvalid, \'ccc-interaction-match-has-min-error\': matchCountMap[source.identifier].matchMinInvalid}">',
                                        '<i class="fa fa-exclamation-triangle ccc-interaction-match-error-icon" ng-if="!interaction.hideInternalResponseValidationErrors" aria-hidden="true"></i>',
                                        '<span class="ccc-cell-header ccc-fg" ng-bind-html="source.trustedContent"></span>',
                                    '</td>',

                                    '<td scope="col" class="ccc-interaction-match-input-cell" ng-repeat="target in source.matches" ng-mouseover="cellFocused($parent.$index, $index)" ccc-click-no-focus="toggleValue($event, target)">',
                                        '<ccc-toggle-input type="checkbox" has-error="!interaction.hideInternalResponseValidationErrors && (matchCountMap[target.identifier].invalid || matchCountMap[source.identifier].invalid)" toggle-value="target.value" data="{source:source, target:target}" input-aria-label="getInputAriaLabel(source.identifier, target.identifier)" tabindex="-1"></ccc-toggle-input>',
                                    '</td>',
                                '</tr>',
                            '</tbody>',
                        '</table>',
                    '</div>',

                    '<span ccc-lang>',
                        '<ul class="ccc-interaction-error-list ccc-fg" ng-class="{\'ccc-interaction-error-list-visible\': interaction.matchMaxValidationErrors.length > 0}" ng-if="interaction.validationErrors.length > 0" aria-live="assertive" aria-relevant="additions" role="status">',
                            '<li tabindex="0" ng-repeat="error in interaction.validationErrors track by error.error" class="invalid-error-message" ng-class="{\'ccc-interaction-error-list-item-live\': error.live}">',
                                '<span ng-bind-html="error.msg | translate"></span>',
                            '</li>',
                        '</ul>',
                    '</span>',

                '</span>'
            ].join('')

        };

    });

})();
