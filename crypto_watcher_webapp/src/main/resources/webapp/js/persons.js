/**
 * Author: Per Spilling, per@kodemaker.no
 */
var myApp = angular.module('persons', ['ngResource', 'ui.bootstrap'], function ($dialogProvider) {
    $dialogProvider.options({backdropClick: false, dialogFade: true});
});

/**
 * Configure the PersonsResource. In order to solve the Single Origin Policy issue in the browser
 * I have set up a Jetty proxy servlet to forward requests transparently to the API server.
 * See the web.xml file for details on that.
 */
myApp.factory('PersonsResource', function ($resource) {
    return $resource('/api/tradable/query?symbol=USDT-BTC&&period=min&&period_num=5', {}, {});
});

myApp.factory('PersonResource', function ($resource) {
    return $resource('/api/persons/:id', {}, {});
});

function PersonsCtrl($scope, PersonsResource, PersonResource, $dialog, $q,$resource) {
    /**
     * Define an object that will hold data for the form. The persons list will be pre-loaded with the list of
     * persons from the server. The personForm.person object is bound to the person form in the HTML via the
     * ng-model directive.
     */
    $scope.personForm = {
        show: true,
        person: {
        }
    }
    //$scope.persons = {};
    $scope.persons = PersonsResource.query();

    /**
     * Function used to toggle the show variable between true and false, which in turn determines if the person form
     * should be displayed of not.
     */
    $scope.togglePersonForm = function () {
        $scope.personForm.show = !$scope.personForm.show;
    }

    /**
     * Clear the person data from the form.
     */
    $scope.clearForm = function () {
        $scope.personForm.person = {}
    }

    $scope.searchCurrency = function () {
            var baseUrl = '/api/tradable/query?symbol='
            var url = baseUrl.concat($scope.personForm.person.currency,'&&period=',
                                        $scope.personForm.person.period,'&&period_num=',
                                        $scope.personForm.person.period_num,'&&interval=',
                                        $scope.personForm.person.interval)
            console.log(url)
            $scope.persons = $resource(url, {}, {}).query();
        }

    /**
     * Save a person. Make sure that a person object is present before calling the service.
     */
/*    $scope.savePerson = function (person) {
        if (person != undefined) {
            *//**
             * Here we need to ensure that the PersonsResource.query() is done after the PersonsResource.save. This
             * is achieved by using the $promise returned by the $resource object.
             *//*
            PersonsResource.save(person).$promise.then(function() {
                $scope.persons = PersonsResource.query();
                $scope.personForm.person = {}  // clear the form
            });
        }
    }*/

    $scope.dt = new Date();

     $scope.options = {
        customClass: getDayClass,
        minDate: new Date(),
        showWeeks: true
      };

      function getDayClass(data) {
          var date = data.date,
            mode = data.mode;
          if (mode === 'day') {
            var dayToCheck = new Date(date).setHours(0,0,0,0);

            for (var i = 0; i < $scope.events.length; i++) {
              var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

              if (dayToCheck === currentDay) {
                return $scope.events[i].status;
              }
            }
          }

          return '';
        }
}

/*
 $scope.kodemakerPersons = {}
 $scope.persons = PersonsResource.query(function (response) {
 angular.forEach(response, function (person) {
 console.log('person.name=' + person.name)
 });
 });
 */