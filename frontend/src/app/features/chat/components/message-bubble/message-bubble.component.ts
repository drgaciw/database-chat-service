import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Message } from '../../models/message.model';

@Component({
  selector: 'app-message-bubble',
  templateUrl: './message-bubble.component.html',
  styleUrls: ['./message-bubble.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MessageBubbleComponent {
  @Input() message!: Message;
  @Input() isStreaming = false;
  @Input() isLast = false;
}
