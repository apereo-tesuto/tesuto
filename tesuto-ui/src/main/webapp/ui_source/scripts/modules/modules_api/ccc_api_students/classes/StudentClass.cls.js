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

    angular.module('CCC.API.Students').factory('StudentClass', [

        'UtilsService',

        function (UtilsService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var StudentClass = function (studentConfigs) {

                var defaults = {
                    cccId: false,
                    collegeStatuses: [],
                    primaryAffiliation: null,
                    displayName: '',
                    firstName: '',
                    lastName: '',
                    middleName: '',
                    phone: '',
                    mobilePhone: '',
                    email: '',
                    age: '',

                    // generated after initialization
                    phoneFormatted: false,  // generated from phone / mobilePhone
                    middleInitial: ''       // generated from middleName
                 };

                // merge in the defaults onto the instance
                var that = this;
                $.extend(true, that, defaults, studentConfigs || {});

                // coerce some values
                that.firstName = that.firstName || '';
                that.lastName = that.lastName || '';
                that.phone = that.phone || '';
                that.mobilePhone = that.mobilePhone || '';
                that.email = that.email || '';
                that.middleName = that.middleName || '';
                that.age = that.age || '';


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                // run through properties and generate additional useful public properties
                var generateStudentMetaData = function () {

                    // normalize the phone number (turn to all digits)
                    that.phone = that.phone.replace(/\D/g,'');

                    // generate formatted phone number
                    var phoneToFormat = that.phone ? that.phone : (that.mobilePhone ? that.mobilePhone : false);
                    that.phoneFormatted = UtilsService.formatTenDigitPhone(phoneToFormat);

                    // generate the middleInitial
                    var hasMiddleName = that.middleName && $.trim(that.middleName).length;
                    if (hasMiddleName) {
                        that.middleInitial = that.middleName.substring(0,1);
                    }
                };


                /*=============== PUBLIC PROPERTIES =============*/

                /*=============== PUBLIC METHODS =============*/


                /*=============== INITIALIZTION =============*/

                generateStudentMetaData();

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return StudentClass;
        }
    ]);

})();
