import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PromptTemplate } from 'src/app/core/services/prompt.service';

@Component({
  selector: 'app-chat-input',
  templateUrl: './chat-input.component.html',
  styleUrls: ['./chat-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChatInputComponent {
  /**
   * The list of prompt templates to display in the menu.
   */
  @Input() promptTemplates: PromptTemplate[] = [];
  /**
   * Whether the component is in a loading state.
   */
  @Input() loading = false;
  /**
   * Emits when the user sends a message.
   */
  @Output() sendMessage = new EventEmitter<string>();

  messageForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.messageForm = this.fb.group({
      message: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  onSendMessage(): void {
    if (this.messageForm.invalid) return;

    const messageContent = this.messageForm.get('message')?.value;
    this.sendMessage.emit(messageContent);
    this.messageForm.reset();
  }

  onEnter(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.onSendMessage();
    }
  }

  onTemplateSelected(template: PromptTemplate): void {
    this.messageForm.get('message')?.setValue(template.text);
  }
}
