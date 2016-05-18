### Instant Runtime Permission Library : For Android Marshmallow
    One can request for permission(single or multiple) in just sinle line of code. 
Demo app is also given illustrating the uses of the library.

#### Uses
```
  PermissionRequest.inside(this)
                .withRequestId("ADD_REQUEST_ID_HERE")
                .forPermissions(new MPermission(Manifest.permission.ACCESS_COARSE_LOCATION , 
                "ADD_EXPLANATION_WHY_THIS_PERMISSION_REQUIRE_IF_USER_DENIES"),
                        new MPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                        "ADD_EXPLANATION_WHY_THIS_PERMISSION_REQUIRE_IF_USER_DENIES"))
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