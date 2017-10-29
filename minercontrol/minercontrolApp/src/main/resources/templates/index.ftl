<!DOCTYPE html>

<html lang="en">
    <head>
        <title>${title}</title>
        <link href="css/bootstrap.css" rel="stylesheet"/>
        <link href="css/app.css" rel="stylesheet"/>
    </head>
    <body ng-app="crudApp">
    
    <!-- NAVIGATION -->
	<nav class="navbar navbar-inverse" role="navigation">
	    <div class="navbar-header">
	        <a class="navbar-brand" ui-sref="#">AngularUI Router</a>
	    </div>
	    <ul class="nav navbar-nav">
	        <li><a ui-sref="home">Home</a></li>
	        <li><a ui-sref="config">Config</a></li>
	    </ul>
	</nav>

        <div ui-view></div>
        <script src="js/lib/angular.min.js" ></script>
        <script src="js/lib/angular-ui-router.min.js" ></script>
        <script src="js/lib/localforage.min.js" ></script>
        <script src="js/lib/ngStorage.min.js"></script>
        <script src="js/app/app.js"></script>
        <script src="js/app/UserService.js"></script>
        <script src="js/app/UserController.js"></script>
        <script src="js/app/ConfigService.js"></script>
        <script src="js/app/ConfigController.js"></script>
    </body>
</html>