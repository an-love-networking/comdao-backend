### Endpoints

- **_please always make the user login again if the error return with error = "Jwt Expired" or "Invalid Jwt"_**

1. Registering

- method: **POST**
- endpoint: _/api/v1/user/register_
- require: none
- input: JSON body

```JSON
{
  "fullname",
  "username",
  "phone",
  "email",
  "password"
}
```

- input requirements: fullname, username and password are mandatory, user can choose to use either email or phone
- output:
    - code 200 OK if success
    - code 409 CONFLICT if either username, email or phone is already taken

2. Login

- method: **POST**
- endpoint: _/api/v1/user/login_
- require: none
- input: JSON body

```JSON
{
  "login_id",
  "password"
}
```

- input requirements: none
- output:
    - code 200 OK with json ``` { "jwt" } ``` if success
    - code 409 CONFLICT if user is disabled
    - code 400 BAD_REQUEST is user is not registered

3. Get profiles, settings

- method: **GET**
- endpoints: _/api/v1/user/profile_, _/api/v1/user/settings_
- require: Header:Authorization = "Bearer [jwt]"
- input: none
- output: JSON body of profile, settings

4. Change profiles, settings and password

- method: **PUT**
- endpoints: _/api/v1/user/profile_, _/api/v1/user/settings_, _/api/v1/user/password_
- require: Header:Authorization = "Bearer [jwt]"
- input:
    - profile: JSON
  ```JSON
  {
    "full_name",
    "username",
    "email",
    "phone",
    "date_of_birth",
    "address"
  }
  ```
    - settings: JSON
  ```JSON
  {
    "should_notify",
    "use_sms",
    "include_promotion",
    "use_dark_mode",
    "use_two_step_verification"
  }
  ```
    - password: JSON
  ```JSON
  {
    "password"
  }
  ```
- output:
    - code 200 OK if success
    - code 400 BAD_REQUEST if user doesnt exists
    - code 409 CONFLICT if user is disabled

5. Disable

- method: **DELETE**
- endpoint: _/api/v1/user/disable?confirm_
- require: Header:Authorization = "Bearer [jwt]"
- input: none
- output:
    - code 200 OK if success
    - code 202 ACCEPTED if confirm=false
    - code 4xx if fail
