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

    angular.module('CCC.API.CompetencyGroups').factory('CompetencyGroupClass', [

        '$q',
        'Cloneable',
        'CompetencyGroupsAPIService',
        'CoursesAPIService',

        function ($q, Cloneable, CompetencyGroupsAPIService, CoursesAPIService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            /*============ CLASS DECLARATION ============*/

            var CompetencyGroupClass = function (configs_in) {

                var defaults = {

                    competencyGroupId: null,    // the system uuid
                    courseId: null,             // foreign key linking to particular class
                    name: '',
                    percent: 50,

                    competencyIds: [],

                    deleting: false,
                    competencyGroupIndex: 0
                };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, configs_in || {});


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                // generate useful meta data from initial config data (from server etc...)
                var initialize = function () {};


                /*=============== PUBLIC PROPERTIES =============*/


                /*=============== PUBLIC METHODS =============*/

                // method used to send data for creation and update
                that.serialize = function () {

                    // this is the format the backend needs for creation and updating
                    var serializeModel = {
                        courseId: that.courseId,
                        name: that.name,
                        percent: that.percent,
                        competencyIds: that.competencyIds
                    };

                    return serializeModel;
                };

                that.create = function () {

                    return CoursesAPIService.createCompetencyGroup(that.courseId, that.serialize()).then(function (competencyGroupId) {
                        // once created merge on properties created server side during the create process
                        that.competencyGroupId = competencyGroupId;
                    });
                };

                that.update = function () {
                    return CompetencyGroupsAPIService.update(that.competencyGroupId, that.serialize());
                };

                that.save = function () {

                    if (that.competencyGroupId) {
                        return that.update();
                    } else {
                        return that.create();
                    }
                };

                that.delete = function () {
                    return CompetencyGroupsAPIService.delete(that.competencyGroupId);
                };


                /*=============== INITIALIZTION =============*/

                initialize();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return CompetencyGroupClass;
        }
    ]);

})();
