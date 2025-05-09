import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule, ErrorHandler } from '@angular/core';
import { ApiService } from './services/api.service';
import { AuthService } from './services/auth.service';
import { ErrorHandlerService } from './services/error-handler.service';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HttpClientModule
  ],
  providers: [
    ApiService,
    AuthService,
    { provide: ErrorHandler, useClass: ErrorHandlerService }
  ]
})
export class CoreModule { }