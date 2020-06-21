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
     * Match class factory
     */

    angular.module('CCC.Assessment').factory('InteractionMatchFactory', [

        '$sce',
        'InteractionFactory',
        '$translate',

        function ($sce, InteractionFactory, $translate) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var getValidateResponsesValidations = function () {

                var that = this;

                var validateResponseValidations = [];

                if (that.maxAssociations && that.valueObjects.length > that.maxAssociations) {

                    var maxErrorMsg = {
                        error: 'interaction.match.maxAssociations.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.MATCH.VALIDATION.GLOBAL_MAX', {MAX: that.maxAssociations}, 'messageformat').then(function (translation) {
                        maxErrorMsg.msg = translation;
                    });

                    validateResponseValidations.push(maxErrorMsg);
                }
                if (that.minAssociations && that.valueObjects.length < that.minAssociations) {

                    var minErrorMsg = {
                        error: 'interaction.match.minAssociations.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.MATCH.VALIDATION.GLOBAL_MIN', {MIN: that.minAssociations}, 'messageformat').then(function (translation) {
                        minErrorMsg.msg = translation;
                    });

                    validateResponseValidations.push(minErrorMsg);
                }

                return validateResponseValidations.concat(that.matchMinValidationErrors).concat(that.matchMaxValidationErrors);
            };

            var getAllowSkippingValidations = function () {

                var that = this;
                var allowSkippingValidations = [];

                // additionally, if there are no errors and no values, then add in the total min error too
                if (that.valueObjects.length === 0) {

                    var allowSkippingError = {
                        error: 'interaction.match.minAssociations.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.MATCH.VALIDATION.GLOBAL_MIN', {MIN: 1}, 'messageformat').then(function (translation) {
                        allowSkippingError.msg = translation;
                    });

                    allowSkippingValidations.push(allowSkippingError);
                }

                return allowSkippingValidations;
            };


            /*============ CLASS DECLARATION ============*/

            var InteractionMatch = function (interactionData) {

                /*=========== DEFAULTS =============*/

                var defaults = {

                    // QTI PROPERTIES : http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10296
                    maxAssociations: 0,     // max associations (lines drawn for the entire interaction)
                    minAssociations: 0,     // min associations (lines drawn for the entire interaction)

                    // VALIDATION METHOD OVERRIDES
                    getAllowSkippingValidations: getAllowSkippingValidations,
                    getValidateResponsesValidations: getValidateResponsesValidations
                };

                // NOTE: This interaction's children (simpleAssociableChoice) are configurable for num associataions they can have (matchMax, matchMin)
                // http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10301


                /*=========== INSTANCE INIT AND CONFIG MERGE ============*/

                // Gather final configs and Extend the InteractionFactory base class
                var that = this;
                var interactionConfigs = $.extend(true, {}, defaults, interactionData, {
                    valueObjects: [],  // used internally to track relationships, these are converted into values the server expects
                    matchMinValidationErrors: [],   // will be used to help determine validity
                    matchMaxValidationErrors: []    // will be used to help determine validity
                });
                InteractionFactory.call(that, interactionConfigs);


                /*========== PRIVATE VARIABLES / METHODS ============*/

                var forceInteger = function (valueToForce, defaultValue) {
                    if (valueToForce === undefined || valueToForce === null || valueToForce === "") {
                        return defaultValue;
                    } else {
                        return parseInt(valueToForce);
                    }
                };

                // this interaction requires some additional massaging to get things setup for validation and rendering
                var validateModel = function () {

                    // coerce some configurations
                    that.maxAssociations = forceInteger(that.maxAssociations, 0);
                    that.minAssociations = forceInteger(that.minAssociations, 0);

                    if (!that.sourceMatchSets || !that.sourceMatchSets || !that.sourceMatchSets.length || !that.targetMatchSets || !that.targetMatchSets.length) {
                        throw new Error('InteractionMatchFactory: simpleMatchSets are not valid for this interaction');
                    }

                    // coerce associableItem configurations and create trustedContent attribute for rendering HTML
                    _.each(that.sourceMatchSets, function (matchSet) {
                        matchSet.matchMax = forceInteger(matchSet.matchMax, 0);
                        matchSet.matchMin = forceInteger(matchSet.matchMin, 0);
                        matchSet.trustedContent = $sce.trustAsHtml(matchSet.content);
                    });
                    _.each(that.targetMatchSets, function (matchSet) {
                        matchSet.matchMax = forceInteger(matchSet.matchMax, 0);
                        matchSet.matchMin = forceInteger(matchSet.matchMin, 0);
                        matchSet.trustedContent = $sce.trustAsHtml(matchSet.content);
                    });
                };


                /*============ INITIALIZATION ============*/

                // For convenience establish more readable sets for matchSets
                that.sourceMatchSets = that.matchSets[0].matchSet;
                that.targetMatchSets = that.matchSets[1].matchSet;

                // now everthing is ready, so run some initial model coercsion and structure checking
                validateModel();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return InteractionMatch;
        }
    ]);

})();


