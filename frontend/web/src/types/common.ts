export interface ErrorDetail {
  code: number;
  message: string;
  type?: string;
}

export interface ErrorResponse {
  error: ErrorDetail;
}
