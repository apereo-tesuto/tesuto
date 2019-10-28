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
     * Re-usable focus related methods
     */

    angular.module('CCC.Components').service('CommonLayoutService', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

        /*============ SERVICE DECLARATION ============*/

        var CommonLayoutService;


        /*============ PRIVATE METHODS AND VARIABLES ============*/

        var appHeaderElement;
        var subnavElement;
        var contentElement;
        var contentHeight = 0;

        var getAppHeaderElement = function () {
            var tempAppHeaderElement = $('.app-header');
            return tempAppHeaderElement.length !== 0 ? tempAppHeaderElement : false;
        };

        var getSubnavElement = function () {
            var tempSubnavElement = $('ccc-view-common-subnav');
            return tempSubnavElement.length !== 0 ? tempSubnavElement : false;
        };

        var getContentElement = function () {
            var tempContentElement = $('.ccc-view-common-content-inner');
            return tempContentElement.length !== 0 ? tempContentElement : false;
        };

        // get height of screen minus header and subnav
        var updateContentHeight = function () {
            var viewportHeight = window.innerHeight;
            appHeaderElement = appHeaderElement || getAppHeaderElement();
            subnavElement = subnavElement || getSubnavElement();
            contentElement = contentElement || getContentElement();

            var headerHeight = appHeaderElement.outerHeight(true);
            var subnavHeight = subnavElement.outerHeight(true);
            contentHeight = viewportHeight - (headerHeight + subnavHeight);

            contentElement.css('height', contentHeight);
            $rootScope.$broadcast('CommonLayoutService.resize');
            return contentHeight;
        };


        /*============ LISTENERS ============*/

        // any child directive on the page can tells us when they focused an element manually
        // this helps us run the updateContentHeight method that fixes the issue when a focused element in a non-scrollable area gets focus and causes the view port to shift
        $rootScope.$on('ccc-assess.element-focused', updateContentHeight);

        $rootScope.$on('ccc-assess.resize', updateContentHeight);


        /*============ SERVICE DEFINITION ============*/

        CommonLayoutService = {

            updateContentHeight: function () {
                return updateContentHeight();
            },

            getContentHeight: function () {
                return contentHeight;
            }

        };


        /*============ SERVICE PASSBACK ============*/

        return CommonLayoutService;

    }]);

})();
