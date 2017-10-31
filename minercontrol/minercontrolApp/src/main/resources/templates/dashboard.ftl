<div class="generic-container">
    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading"><span class="lead">Control Miner </span></div>
		<div class="panel-body">
	        <div class="formcontainer">
	            <div class="alert alert-success" role="alert" ng-if="ctrl.successMessage">{{ctrl.successMessage}}</div>
	            <div class="alert alert-danger" role="alert" ng-if="ctrl.errorMessage">{{ctrl.errorMessage}}</div>
	                <div class="row">
	                    <div class="form-actions">
	                        <button type="button" ng-click="ctrl.reset()" class="btn btn-warning btn-sm" ng-disabled="myForm.$pristine">{{!ctrl.start ? 'Start' : 'Stop'  }}</button>
	                    </div>
	                </div>
    	    </div>
		</div>	
    </div>
    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading"><span class="lead">Config</span></div>
		<div class="panel-body">
	        <div class="formcontainer">
	            <div class="alert alert-success" role="alert" ng-if="ctrl.successMessage">{{ctrl.successMessage}}</div>
	            <div class="alert alert-danger" role="alert" ng-if="ctrl.errorMessage">{{ctrl.errorMessage}}</div>
	            <form ng-submit="ctrl.submit()" name="myForm" class="form-horizontal">
	                <div class="row">
	                    <div class="form-group col-md-12">
	                        <label class="col-md-2 control-lable" for="uname">BTC Address</label>
	                        <div class="col-md-7">
	                            <input type="text" ng-model="ctrl.config.btcAddress" id="uname" class="configname form-control input-sm" placeholder="Enter your BTC Address" required ng-minlength="3"/>
	                        </div>
	                    </div>
	                </div>
<!-- 	                <div class="row"> -->
<!-- 	                    <div class="form-group col-md-12"> -->
<!-- 	                        <label class="col-md-2 control-lable" for="uname">usernameMiningPoolHub</label> -->
<!-- 	                        <div class="col-md-7"> -->
<!-- 	                            <input type="text" ng-model="ctrl.config.usernameMiningPoolHub" id="uname" class="configname form-control input-sm" placeholder="Enter your name" required ng-minlength="3"/> -->
<!-- 	                        </div> -->
<!-- 	                    </div> -->
<!-- 	                </div> -->
	                <div class="row">
<!-- 	                {{ctrl.config}} -->
	                    <div class="form-group col-md-12">
	                        <label class="col-md-2 control-lable" for="uname">Worker name</label>
	                        <div class="col-md-7">
	                            <input type="text" ng-model="ctrl.config.workername" id="uname" class="configname form-control input-sm" placeholder="Enter your Worker Name" required ng-minlength="3"/>
	                        </div>
	                    </div>
	                </div>

	                <div class="row">
	                    <div class="form-actions floatRight">
	                        <input type="submit"  value="Update" class="btn btn-primary btn-sm" >
<!-- 	                        <button type="button" ng-click="ctrl.reset()" class="btn btn-warning btn-sm" ng-disabled="myForm.$pristine">Reset Form</button> -->
<!-- 	                        <td><button type="button" ng-click="ctrl.editConfig('1')" class="btn btn-success custom-width">Edit</button></td> -->
	                    </div>
	                </div>
	            </form>
    	    </div>
		</div>	
    </div>
    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading"><span class="lead">List Mining </span></div>
		<div class="panel-body">
			<div class="table-responsive">
		        <table class="table table-hover">
		            <thead>
		            <tr>
		                <th>Speed Power</th>
		                <th>miner</th>
		                <th>alg</th>
		                <th>pool</th>
		                <th>Pool Rate Coin/Day</th>
		                <th>Coin/Day</th>
		                <th>USD/Day</th>
		                <th width="100"></th>
		            </tr>
		            </thead>
		            <tbody>
		            <tr ng-repeat="u in ctrl.configs">
		                <td>{{u.speed}}</td>
		                <td>{{u.miner.name}}</td>
		                <td>{{u.alg}}</td>
		                <td>{{u.pool.name}}</td>
		                <td>{{u.rate}}</td>
		                <td>{{u.coinPerDay}}</td>
		                <td>{{u.usdPerDay}}</td>
		                <td><button type="button" ng-click="ctrl.editConfig('1')" class="btn btn-success custom-width">Edit</button></td>
		            </tr>
		            </tbody>
		        </table>		
			</div>
		</div>
    </div>
</div>