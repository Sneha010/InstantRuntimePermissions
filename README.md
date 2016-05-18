### Instant Runtime Permission Library : For Android Marshmallow
    One can request for permission(single or multiple) in just sinle line of code. 
Demo app is also given illustrating the uses of the library.

#### Uses
```
  PermissionRequest.inside(this)
                   .withRequestId("ADD_REQUEST_ID_HERE")
                   .forPermissions( new MPermission("PERMISSION1" , 
                                    "ADD_EXPLANATION_WHY_THIS_PERMISSION_REQUIRED_IF_USER_DENIES"),
                                    new MPermission("PERMISSION2" ,
                                    "ADD_EXPLANATION_WHY_THIS_PERMISSION_REQUIRED_IF_USER_DENIES"))
                   .request(new PermissionRequest.GrantPermissionListener() {
                        @Override
                        public void grantedPermission(List<String> permissions) {

                            // Perform success task
                        }

                        @Override
                        public void rejected(List<String> permissions) {
                    
                            // Do nothing or show error
                      
                        }
                });
                
```


### License
```
Copyright 2016 Sneha Khadatare

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```