import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandler, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService implements ErrorHandler {
  handleError(error: any): void {
    if (error instanceof HttpErrorResponse) {
      // Handle HTTP errors
      console.error('HTTP Error:', error);
      // You can add more specific error handling here
    } else {
      // Handle other types of errors
      console.error('An error occurred:', error);
    }
  }
} 