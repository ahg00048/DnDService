user_name = 'admin'
user_pwd = 'admin'

db.createUser(
  {
    user: user_name,
    pwd: user_pwd,
    roles: [ 'root' ]
  },
);