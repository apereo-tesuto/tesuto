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

    // this directive allows the keyboard nav logic to work with different inner markup within a dropdown
    // so you can do li > a  or li > span or whatever as long as there is an item with '.dropdown-menu-item' class
    // this gives flexibility in accessibility to use different markup and roles especially when it comes
    // to getting mathml to render correctly in the dropdown items

    angular.module('CCC.Assessment').directive('cccDropdownKeyboardNav', function () {
        return {

            restrict: 'A',

            require: '?^uibDropdown',

            link: function (scope, element, attrs, dropdownCtrl) {

                element.bind('keydown', function (e) {

                    // up or down
                    if ([38, 40].indexOf(e.which) !== -1) {
                        e.preventDefault();
                        e.stopPropagation();

                        var elems = dropdownCtrl.dropdownMenu.find('.dropdown-menu-item');

                        switch (e.which) {
                            case (40): { // Down
                                if (!angular.isNumber(dropdownCtrl.selectedOption)) {
                                    dropdownCtrl.selectedOption = 0;
                                } else {
                                    dropdownCtrl.selectedOption = dropdownCtrl.selectedOption === elems.length -1 ?
                                    dropdownCtrl.selectedOption : dropdownCtrl.selectedOption + 1;
                                }
                                break;
                            }
                            case (38): { // Up
                                if (!angular.isNumber(dropdownCtrl.selectedOption)) {
                                    dropdownCtrl.selectedOption = elems.length - 1;
                                } else {
                                    dropdownCtrl.selectedOption = dropdownCtrl.selectedOption === 0 ?
                                    0 : dropdownCtrl.selectedOption - 1;
                                }
                                break;
                            }
                        }
                        elems[dropdownCtrl.selectedOption].focus();

                    // enter key
                    } else if (e.which === 13) {

                        var isEnterOnToggle = $(e.target).hasClass('dropdown-toggle');

                        // we only force close when enter was hit from one of the dropdown items
                        if (dropdownCtrl.isOpen() && !isEnterOnToggle) {
                            dropdownCtrl.toggle(false);
                        }
                    }
                });
            }
        };
    });

})();
