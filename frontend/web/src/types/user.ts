export interface BaseUser {
  email: string;
  password: string;
}

export interface NewUser extends BaseUser {
  email: string;
  captchaCode: string;
}

export interface UserResponse {
  email: string;
  token: string;
}
