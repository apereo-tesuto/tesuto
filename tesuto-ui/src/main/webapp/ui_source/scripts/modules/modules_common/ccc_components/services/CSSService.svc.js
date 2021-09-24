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

    angular.module('CCC.Components').service('CSSService', [

        '$rootScope',

        function ($rootScope) {

            var CSSService;

            /*============== PRIVATE VARIABLES ==============*/


            var LOADED_CSS_MAP = {};
            var LOADED_CSS_GROUPS = {};

            var removeKeyFromGroup = function (key, groupKey) {

                var group = LOADED_CSS_GROUPS[group] || [];

                var index = group.indexOf(key);
                group.splice(index, 1);
            };


            var LOADED_DYNAMIC_STYLES = {};

            var addRule = function (styleList, id) {

                var styleElement = document.createElement("style");
                var sheet = document.head.appendChild(styleElement).sheet;

                var propText;
                _.each(styleList, function (styleObj) {

                    propText = Object.keys(styleObj.styles).map(function(p){
                        return p+":"+styleObj.styles[p];
                    }).join(";");

                    sheet.insertRule(styleObj.selector + "{" + propText + "}", sheet.cssRules.length);
                });

                LOADED_DYNAMIC_STYLES[id] = styleElement;
            };


            /*============== SERVICE DEFINITION ==============*/

            CSSService = {

                // having groups allows us to make sure we have unique individual styles but can blast all by groupName
                unloadStylesGroup: function (group) {

                    var groupList = LOADED_CSS_GROUPS[group] || [];
                    _.each(groupList, function (key) {
                        CSSService.unloadStyles(key);
                    });
                },

                unloadStyles: function (key) {

                    var keyGroup = key.group;

                    if (keyGroup) {
                        removeKeyFromGroup(key, keyGroup);
                    }

                    delete LOADED_CSS_MAP[key];

                    var styleElement = $('#css-service-id-' + key);
                    styleElement.attr('disabled', 'disabled'); // recommended, not sure if needed

                    styleElement.remove();
                },

                // the key must be unique, so a repeat key will replace an existing loaded style
                // pass in an optional group, so you can clear styles by a group name
                loadStyles: function (key, styleString, group) {

                    if (group) {

                        if (!LOADED_CSS_GROUPS[group]) {
                            LOADED_CSS_GROUPS[group] = [];
                        }

                        LOADED_CSS_GROUPS[group].push(key);
                    }

                    if (LOADED_CSS_MAP[key]) {
                        CSSService.unloadStyles(key);
                    }

                    var styleElement = $('<style type="text/css" id="css-service-id-' + key + '"></style>');
                    styleElement.text(styleString);

                    $('head').append(styleElement);

                    // store the group on the key map so we can update the groups array if an individual style key is removed
                    LOADED_CSS_MAP[key] = {group: group || false};
                },

                /**
                 * this is different from the previous methods. Here we don't have a styleString, we want to add styles by a json configuration
                 * @param {Array} styleList        an array of objects of this form {selector: 'p:before', styles: {width: '50px'}}
                 * @param {String} id              if you provide an id, it will check the loaded map.. if it already exists then it won't reload it
                 */
                addDynamicStyles: function (styleList, id) {

                    if (id && LOADED_DYNAMIC_STYLES[id]) {
                        return false;
                    }

                    addRule(styleList, id);
                }
            };

            return CSSService;
        }
    ]);

})();
