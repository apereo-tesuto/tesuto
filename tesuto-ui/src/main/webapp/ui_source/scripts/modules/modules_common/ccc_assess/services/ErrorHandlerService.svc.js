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
     * A service to gather errors. Allows all server calls and other methods to use this service
     * The handling can be configured at a later time and can be different based on the endpoints configuration
     * This service is really a hook for all errors, another layer, but is also a pass through to allow the caller to handler the error as well
     */

    angular.module('CCC.Assess').service('ErrorHandlerService', [

        '$rootScope',
        '$q',

        function ($rootScope, $q) {

            /*============ SERVICE DECLARATION ============*/

            var ErrorHandlerService;


            /*============ PRIVATE VARIABLES ============*/

            // TYPE TO ARRAY OF CHANNELS TO CALL
            var errorTypeChannelsMap = {
                '404': ['default'],
                '401': ['default'],
                '403': ['default'],
                '500': ['default']
            };

            // CHANNEL CALLBACKS THAT RECIEVE THE TYPE AND ERROR
            var errorChannels = {
                default: function (type, err) {
                    throw new Error('No default error handler set');
                }
            };


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            // todo: make this congfigurable during app bootstrap process
            var parseServerError = function (err) {
                return {type: err.status + '', err: err};
            };

            var getErrorChannels = function (errType) {
                return errorTypeChannelsMap[errType] || [];
            };

            var getAllErrorTypeKeys = function () {
                return _.keys(errorTypeChannelsMap);
            };


            /*============ SERVICE DEFINITION ============*/

            ErrorHandlerService = {

                /**
                 * Method allows you to pass in an object with key value pairs and ovewrite at any time
                 * @param {hashMap} errorChannels_in key value pairs  key: function (type, err) {}
                 */
                setErrorChannels: function (errorChannels_in) {
                    // overlay incoming error channels (SEE ABOVE FOR EXAMPLE)
                    $.extend(errorChannels, errorChannels_in);
                },

                /**
                 * Method allows you to pass in an object with key value pairs and ovewrite at any time
                 * @param {hashMap} errorTypeChannelsMap_in key value pairs key: ['channel1', 'channel2']
                 */
                setErrorTypeChannelsMap: function (errorTypeChannelsMap_in) {
                    // overlay incoming errorTypeChannelsMap (SEE ABOVE FOR EXAMPLE)
                    $.extend(errorTypeChannelsMap, errorTypeChannelsMap_in);
                },

                /**
                 * [addErrorChannel description]
                 * @param {string} errorChannelKey     string key
                 * @param {function} errorChannelHandler function to be called for this error channel
                 */
                addErrorChannel: function (errorChannelKey, errorChannelHandler) {
                    errorChannels[errorChannelKey] = errorChannelHandler;
                },

                /**
                 * [addErrorChannelToErrorTypes description]
                 * @param {[type]} errorChannelKey the channel key to add
                 * @param {[type]} errorTypesList  the list of errorTypes to add the channel key to. If empty will be all
                 */
                addErrorChannelToErrorTypes: function (errorChannelKey, errorTypeList) {

                    if (!errorTypeList || errorTypeList.length === 0) {
                        errorTypeList = getAllErrorTypeKeys();
                    }

                    // push in the errorChannel to each errorType in the erroTypeList
                    _.each(errorTypeList, function (errorTypeKey) {
                        errorTypeChannelsMap[errorTypeKey].push(errorChannelKey);
                    });
                },

                // will return a rejected promise so that the http promise can return a rejected promise
                // this allows this default service to hook into errors but also allow the calling promise to fail and handle the error as well
                reportError: function (type, err, channelBlackList) {

                    channelBlackList = channelBlackList || [];

                    try {

                        // report this error through all configured error channels for that error type
                        var errorChannelList = getErrorChannels(type);

                        _.each(errorChannelList, function (errorChannel) {

                            var errorChannelHandler = errorChannels[errorChannel];

                            // if this error handler exists and this channel was not blacklisted for this report
                            if (errorChannelHandler && channelBlackList.indexOf(errorChannel) === -1) {

                                errorChannelHandler(type, err);

                            // otherwise let's try a default error handler
                            } else {
                                errorChannels['default'](type, err);
                            }
                        });

                        return $q.reject(err);

                    } catch (e) {
                        return $q.reject({type: 'registerdEventChannelError', err: e});
                    }
                },

                // this method runs a server error object through a parser and maps it to a configured type and err
                reportServerError: function (err, allowableErrorTypes_in, channelBlackList) {

                    var allowableErrorTypes = allowableErrorTypes_in || [];
                    var parsedError = parseServerError(err);

                    // if this type is not allowable, then carry on and report it (note, numbers are cast to strings)
                    if (allowableErrorTypes.indexOf(parsedError.type + '') === -1) {

                        return ErrorHandlerService.reportError(parsedError.type, parsedError.err, channelBlackList);

                    // otherwise, just avoid reporting it and reject it and let the caller deal with it
                    } else {
                        return $q.reject(parsedError.err);
                    }
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return ErrorHandlerService;

        }
    ]);

})();

