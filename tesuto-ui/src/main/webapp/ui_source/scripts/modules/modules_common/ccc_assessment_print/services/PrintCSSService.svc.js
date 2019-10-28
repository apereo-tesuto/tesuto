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
     * This service generally does a recusive search on arrays and object looking for "stylesheets" values and loads them all using the CSSService
     */
    angular.module('CCC.AssessmentPrint').service('PrintCSSService', [

        'CSSService',

        function (CSSService) {

            /*============ SERVICE DECLARATION ============*/

            var PrintCSSService;


            /*============ PRIVATE VARIABLES AND METHODS ============*/

            var extractStylesFromArray;
            var extractStylesFromObject;

            var isArray = function (objectInQuestion) {
                return Object.prototype.toString.call(objectInQuestion) === '[object Array]';
            };

            // recursive array search
            extractStylesFromArray = function (searchArray) {

                var stylesheets = [];

                var currentItem;
                for (var i=0; i < searchArray.length; i++) {

                    currentItem = searchArray[i];

                    if (isArray(currentItem)) {
                        stylesheets = stylesheets.concat(extractStylesFromArray(currentItem));
                    } else if (typeof currentItem === 'object') {
                        stylesheets = stylesheets.concat(extractStylesFromObject(currentItem));
                    }
                }

                return stylesheets;
            };

            // recursive object search
            extractStylesFromObject = function (searchObject) {

                var stylesheets = [];

                var currentValue;
                for (var key in searchObject) {
                    if (searchObject.hasOwnProperty(key)) {

                        currentValue = searchObject[key];

                        // if we found a stylesheets property, push in the stylesheet string
                        if (key === 'stylesheets') {

                            stylesheets.push(currentValue);

                        } else {

                            if (isArray(currentValue)) {

                                stylesheets = stylesheets.concat(extractStylesFromArray(currentValue));

                            } else if (typeof currentValue === 'object') {

                                stylesheets = stylesheets.concat(extractStylesFromObject(currentValue));
                            }
                        }

                    }
                }

                return stylesheets;
            };


            /*============ PUBLIC METHODS AND PROPERTIES ============*/

            PrintCSSService = {

                findAndloadStyles: function (searchObject) {

                    var stylesheets;

                    if (isArray(searchObject)) {
                        stylesheets = extractStylesFromArray(searchObject);
                    } else {
                        stylesheets = extractStylesFromObject(searchObject);
                    }

                    // now that we have recursively looked for stylesheets... load them
                    _.each(stylesheets, function (styleString, styleStringIndex) {
                        CSSService.loadStyles('print-' + styleStringIndex, styleString);
                    });
                }
            };


            /*============ SERVICE PASS BACK ============*/

            return PrintCSSService;
        }
    ]);

})();
