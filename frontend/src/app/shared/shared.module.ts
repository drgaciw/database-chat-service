import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { MarkdownModule } from 'ngx-markdown';

import { FooterComponent } from './components/footer/footer.component';
import { HeaderComponent } from './components/header/header.component';
import { MarkdownPipe } from './pipes/markdown.pipe';

const materialModules = [
  MatButtonModule,
  MatInputModule,
  MatFormFieldModule,
  MatCardModule,
  MatIconModule,
  MatMenuModule,
  MatProgressSpinnerModule,
  MatSnackBarModule,
  MatTooltipModule
];

@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    MarkdownPipe
  ],
  imports: [
    CommonModule,
    RouterModule,
    ...materialModules,
    MarkdownModule.forChild()
  ],
  exports: [
    ...materialModules,
    RouterModule,
    HeaderComponent,
    FooterComponent,
    MarkdownPipe,
    MarkdownModule
  ]
})
export class SharedModule { }