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
var MutationObserver = MutationObserver || false;

angular.module('CCC.View.Common').directive('cccHelp', function () {

        return {

            restrict: 'E',

            scope: {
                isStudent: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$sce',

                function ($scope, $element, $sce) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var searchResults = $element.find('#gsfn_search_results')[0];

                    var searchUrl = {
                        student: 'https://getsatisfaction.com/ccchelp/widgets/javascripts/1711c2baca/widgets.js?v=${git.commit.id.abbrev}',
                        admin: 'https://getsatisfaction.com/ccctc/widgets/javascripts/1711c2baca/widgets.js?v=${git.commit.id.abbrev}'
                    };


                    /*============ MODEL ============*/

                    $scope.isStudent = $scope.isStudent || false;

                    $scope.formAction = $scope.isStudent ? $sce.trustAsResourceUrl('https://getsatisfaction.com/ccchelp') : $sce.trustAsResourceUrl('https://getsatisfaction.com/ccctc');


                    /*============ MODEL DEPENDENT METHODS =============*/

                    var injectSearchScript = function () {
                        var head = document.getElementsByTagName('head')[0];

                        var script = document.createElement('script');
                        script.src = $scope.isStudent ? searchUrl.student : searchUrl.admin;
                        script.type = 'text/javascript';

                        head.appendChild(script);
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    if (MutationObserver) {

                        // Using native mutationObserver to detect when searchResults are loaded
                        var observer = new MutationObserver(function () {
                            $(searchResults).find('a').attr('target', '_blank');
                            $(searchResults).find('li > input').addClass('btn btn-default');
                        });

                        observer.observe(searchResults, {
                            childList: true
                        });
                    }


                    /*============ INITIALIZATION ============*/

                    injectSearchScript();

                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col col-md-8">',
                        '<h2 translate="CCC_VIEW_STANDARD_COMMON.CCC-HELP.ASK.TITLE"></h2>',

                        // 3rd party help form
                        '<div class="gsfn_content">',
                            '<form accept-charset="utf-8" action="{{formAction}}" id="gsfn_search_form" method="get" onsubmit="gsfn_search(this); return false;">',

                                '<div class="well well-sm">',
                                    '<p id="gsfn_label" translate="CCC_VIEW_STANDARD_COMMON.CCC-HELP.ASK.DESC"></p>',
                                    '<div class="row">',
                                        '<div class="col col-xs-12 questions">',
                                            '<span ng-if="!isStudent" translate="CCC_VIEW_STANDARD_COMMON.CCC-HELP.ASK.ADMIN"></span>',
                                            '<span ng-if="isStudent" translate="CCC_VIEW_STANDARD_COMMON.CCC-HELP.ASK.STUDENT"></span>',
                                        '</div>',
                                    '</div>',
                                    '<div class="input-group">',
                                        '<input name="style" type="hidden" value="" />',
                                        '<input name="limit" type="hidden" value="10" />',
                                        '<input name="utm_medium" type="hidden" value="widget_search" />',
                                        '<input name="utm_source" type="hidden" value="widget_ccchelp" />',
                                        '<input name="callback" type="hidden" value="gsfnResultsCallback" />',
                                        '<input name="format" type="hidden" value="widget" />',
                                        '<input id="gsfn_search_query" aria-labelledby="gsfn_label" maxlength="120" name="query" type="text" class="form-control" placeholder="Search for...">',
                                        '<span class="input-group-btn">',
                                            '<input id="continue" class="btn btn-primary" type="submit" value="Go!">',
                                        '</span>',
                                    '</div>',
                                '</div>',
                            '</form>',

                            // Search results will populate here
                            '<div id="gsfn_search_results" style="height: auto;"></div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });
