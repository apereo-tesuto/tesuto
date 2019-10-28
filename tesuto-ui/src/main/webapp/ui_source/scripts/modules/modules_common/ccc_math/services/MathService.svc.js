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
var MathJax = MathJax || {};

(function () {

    /**
     * Wrap the MathJax API to normalize some of it's services
     */

    angular.module('CCC.Math').service('MathService', [

        '$rootScope',
        '$timeout',
        'CCCUtils',

        function ($rootScope, $timeout, CCCUtils) {

            /*============ SERVICE DECLARATION ============*/

            var MathService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            MathService = {


                render: function (renderCallBack) {

                    renderCallBack = renderCallBack || $.noop;

                    // hide all the math
                    $('body').removeClass('show-math');

                    setTimeout(function () {

                        // pre and process
                        MathJax.Hub.Queue(["PreProcess", MathJax.Hub]);
                        MathJax.Hub.Queue(["Process", MathJax.Hub]);

                        // when it is done processing, then show the math
                        MathJax.Hub.Queue(["callBack", {
                            callBack: function () {
                                $('body').addClass('show-math');
                                renderCallBack();
                            }
                        }]);
                    }, 1);
                },

                rerender: function (rerenderCallBack) {

                    rerenderCallBack = rerenderCallBack || $.noop;

                    // hide all the math
                    $('body').removeClass('show-math');

                    setTimeout(function () {

                        // rerender
                        MathJax.Hub.Queue(["Rerender", MathJax.Hub]);

                        // when it is done processing, then show the math
                        MathJax.Hub.Queue(["callBack", {
                            callBack: function () {
                                $('body').addClass('show-math');
                                rerenderCallBack();
                            }
                        }]);
                    }, 1);
                },

                processTarget: function (target, renderCallBack) {

                    renderCallBack = renderCallBack || $.noop;

                    // hide all the math
                    $(target).addClass('hide-math');

                    setTimeout(function () {
                        // when it is done processing, then show the math
                        MathJax.Hub.PreProcess($(target)[0], function () {
                            MathJax.Hub.Process($(target)[0], function () {

                                $(target).removeClass('hide-math');
                                renderCallBack();
                            });
                        });
                    }, 1);
                },

                setRenderer: function (renderer, doNotPersist) {
                    MathJax.Hub.setRenderer(renderer);
                    if (!doNotPersist) {
                        MathJax.HTML.Cookie.Set("menu", CCCUtils.cleanObject(MathJax.Hub.config.menuSettings));
                    }
                    MathService.rerender();
                },

                getRenderer: function () {
                    return MathJax.Hub.config.menuSettings.renderer;
                }

            };

            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return MathService;

        }
    ]);

})();

