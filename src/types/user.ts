export interface BaseUser {
  username: string;
  password: string;
}

export interface NewUser extends BaseUser {
  email: string;
  captchaCode: string;
}

export interface UserResponse {
  username: string;
  token: string;
}
