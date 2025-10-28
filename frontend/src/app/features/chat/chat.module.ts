import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { ChatInputComponent } from './components/chat-input/chat-input.component';
import { MessageBubbleComponent } from './components/message-bubble/message-bubble.component';
import { RoleToggleComponent } from './components/role-toggle/role-toggle.component';
import { ChatComponent } from './components/chat/chat.component';
import { ChatService } from './services/chat.service';
import { ChatEffects } from './store/chat.effects';
import { chatReducer } from './store/chat.reducer';

@NgModule({
  declarations: [ChatComponent, ChatInputComponent, MessageBubbleComponent, RoleToggleComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSnackBarModule,
    MatTooltipModule,
    StoreModule.forFeature('chat', chatReducer),
    EffectsModule.forFeature([ChatEffects])
  ],
  providers: [ChatService],
  exports: [ChatComponent]
})
export class ChatModule { }
