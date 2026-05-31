### Product endpoints

1. View products

- method: **GET**
- endpoint: _/api/v1/product?page=&size=&filter_content=_
    - page:number = what page index to request (start 0)
    - size:number = how many items per page
    - filter_content:string = search the list for items with that string in the name
- input: none
- output: JSON

```json
{
  "content": [
    products...
  ],
  "empty": boolean,
  "first": boolean,
  "last": boolean,
  "number": int,
  "number_of_elements": int,
  "size": int,
  "total_elements": int,
  "total_pages": int,
  ...
}
```

2. Create product

- method: **POST**
- endpoint: _/api/v1/product_
- requirement:
    - Header:Authorization "Bearer [jwt]"
    - user:Role = ADMIN
- input: JSON

```json
{
  "list": [
    {
      "label": string,
      "description": string,
      "price": double,
      "currency": string,
      "badge": null
      or
      string(
      "HOT",
      "NEW"
      )
    },
    ...
  ]
}
```

- output:
    - code 200 OK if success
    - code 401 UNAUTHORIZED if an USER tried to access this endpoint
    - code 409 CONFLICT if create an existed item

3. Update product

- method: **PUT**
- endpoint: _/api/v1/product_
- requirement:
    - Header:Authorization "Bearer [jwt]"
    - user:Role = ADMIN
- input: JSON

```json
{
  "id": int,
  "label": string,
  "description": string,
  "price": double,
  "currency": string,
  "badge": null
  or
  string(
  "HOT",
  "NEW"
  ),
  "retrievable": boolean
}
```

- output:
    - code 200 OK with the updated product on success
    - code 4xx if fail
