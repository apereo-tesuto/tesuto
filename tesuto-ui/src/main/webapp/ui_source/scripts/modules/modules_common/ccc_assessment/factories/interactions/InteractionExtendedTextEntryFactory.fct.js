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
     * ExtendedInteractionTextEntry class factory
     */

    angular.module('CCC.Assessment').factory('InteractionExtendedTextEntryFactory', [

        '$translate',
        'InteractionFactory',
        'InteractionUtilsService',

        function ($translate, InteractionFactory, InteractionUtilsService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var getValidateResponsesValidations = function () {

                var that = this;

                var validateResponseValidations = [];

                var totalStrings = InteractionUtilsService.getWordsFromString(that.values[0]).length;

                var meetsMinStrings = that.minStrings === 0 || (totalStrings >= that.minStrings);
                var meetsMaxStrings = that.maxStrings === 0 || (that.maxStrings > 0 && totalStrings <= that.maxStrings);

                that.validationErrors = [];

                if (meetsMinStrings && !meetsMaxStrings) {

                    var maxStringsError = {
                        error: 'interaction.extendedTextEntry.maxStrings.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.EXTENDED_TEXT_ENTRY.VALIDATION.MAX', {MAX: that.maxStrings}).then(function (translation) {
                        maxStringsError.msg = translation;
                    });

                    validateResponseValidations.push(maxStringsError);
                }

                if (!meetsMinStrings && meetsMaxStrings) {

                    var minStringsError = {
                        error: 'interaction.extendedTextEntry.minStrings.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.EXTENDED_TEXT_ENTRY.VALIDATION.MIN', {MIN: that.minStrings}, 'messageformat').then(function (translation) {
                        minStringsError.msg = translation;
                    });

                    validateResponseValidations.push(minStringsError);
                }

                return validateResponseValidations;
            };

            var getAllowSkippingValidations = function () {

                var that = this;
                var allowSkippingValidations = [];

                var totalStrings = InteractionUtilsService.getWordsFromString(that.values[0]).length;
                var meetsMinStrings = (totalStrings >= 1);

                // if there are no responseValidations and they do not have at least one word
                if (!meetsMinStrings) {

                    var allowSkippingError = {
                        error: 'interaction.extendedTextEntry.minStrings.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.EXTENDED_TEXT_ENTRY.VALIDATION.MIN', {MIN: 1}, 'messageformat').then(function (translation) {
                        allowSkippingError.msg = translation;
                    });

                    allowSkippingValidations.push(allowSkippingError);
                }

                return allowSkippingValidations;
            };


            /*============ CLASS DECLARATION ============*/

            var InteractionExtendedTextEntry = function (interactionData) {

                /*============= DEFAULTS =============*/

                var defaults = {

                    // QTI Properties: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10334
                    expectedLines: 4,
                    minStrings: 0,
                    maxStrings: 0,
                    expectedLength: 0, // we don't really use this, it is normally used to set the width of a text input

                    // VALIDATION METHOD OVERRIDES
                    getAllowSkippingValidations: getAllowSkippingValidations,
                    getValidateResponsesValidations: getValidateResponsesValidations
                };


                /*=========== INSTANCE INIT AND CONFIG MERGE ============*/

                // Gather final configs and Extend the InteractionFactory base class
                var that = this;
                var interactionConfigs = $.extend(true, {}, defaults, interactionData);
                InteractionFactory.call(that, interactionConfigs);


                /*=========== PRIVATE VARIABLES AND METHODS =============*/

                var MINIMUM_EXPECTED_LINES = 6;

                var validateModel = function () {
                    // coerce some values
                    that.expectedLength = that.expectedLength === null ? 0 : parseInt(that.expectedLength);
                    that.expectedLines = that.expectedLines === null ? 0 : parseInt(that.expectedLines);
                    that.minStrings = that.maxStrings === null ? 0 : parseInt(that.minStrings);
                    that.maxStrings = that.maxStrings === null ? 0 : parseInt(that.maxStrings);

                    // ya know, if you are doing extended text it can be a really bad experience to not have at least this many lines
                    that.expectedLines = Math.max(MINIMUM_EXPECTED_LINES, that.expectedLines);
                };


                /*============= INITIALIZATION =============*/

                // now everthing is ready, so run some initial model coercsion and structure checking
                validateModel();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return InteractionExtendedTextEntry;

        }
    ]);

})();


