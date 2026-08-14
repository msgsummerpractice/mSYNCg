type BackendFieldError = {
  field: string;
  reason: string;
};

type BackendErrorResponse = {
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: BackendFieldError[];
};