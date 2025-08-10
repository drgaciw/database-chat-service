import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Message } from '../../models/message.model';

@Component({
  selector: 'app-message-bubble',
  templateUrl: './message-bubble.component.html',
  styleUrls: ['./message-bubble.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MessageBubbleComponent {
  /**
   * The message to display.
   */
  @Input() message!: Message;
  /**
   * Whether the message is currently streaming.
   */
  @Input() isStreaming = false;
  /**
   * Whether this is the last message in the list.
   */
  @Input() isLast = false;
}
