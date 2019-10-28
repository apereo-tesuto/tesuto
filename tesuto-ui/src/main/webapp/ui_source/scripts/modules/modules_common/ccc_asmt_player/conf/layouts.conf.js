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

    /*========== LAYOUT DEFINITIONS FOR DIFFERENT TEST ITEMS ============*/

    angular.module('CCC.AsmtPlayer').value('LAYOUTS', {

        // single stimulus single items
        'single_column': {
            template: [
                '<div class="container-fluid layout-1">',

                    '<div class="row">',
                        '<div class="col-md-12">',

                            '<div class="layout-container-stimulus"></div>',

                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-md-12">',

                            '<div class="layout-container-items"></div>',

                        '</div>',
                    '</div>',

                '</div>'
            ].join('')
        },

        // no stimulus single items
        'single_column_no_stimulus': {
            template: [
                '<div class="container-fluid layout-1">',

                    '<div class="layout-container-stimulus hidden"></div>',

                    '<div class="row">',
                        '<div class="col-md-12">',

                            '<div class="layout-container-items"></div>',

                        '</div>',
                    '</div>',

                '</div>'
            ].join('')
        },


        'double_column_stimulus_left': {
            template: [
                '<div class="container-fluid layout-1">',

                    '<div class="row">',

                        '<div class="col-md-6">',
                            '<div class="layout-container-stimulus"></div>',
                        '</div>',

                        '<div class="col-md-6">',
                            '<div class="layout-container-items"></div>',
                        '</div>',

                    '</div>',

                '</div>'
            ].join('')
        }

    });

})();
