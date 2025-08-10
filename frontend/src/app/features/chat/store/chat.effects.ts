import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of, concat } from 'rxjs';
import { catchError, map, mergeMap, withLatestFrom } from 'rxjs/operators';
import { AiService } from 'src/app/core/services/ai.service';
import { ModelConfigService } from 'src/app/core/services/model-config.service';
import { ChatService } from '../services/chat.service';
import * as ChatActions from './chat.actions';
import { selectChatState } from './chat.selectors';
import { Message } from '../models/message.model';

@Injectable()
export class ChatEffects {
  // Load Messages Effect
  loadMessages$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.loadMessages),
      withLatestFrom(this.store.select(selectChatState)),
      mergeMap(([action, state]) =>
        this.chatService.getMessages(action.parentId, action.page).pipe(
          map((messages) =>
            ChatActions.loadMessagesSuccess({
              messages,
              hasMore: messages.length === 50
            })
          ),
          catchError((error) =>
            of(ChatActions.loadMessagesFailure({ error }))
          )
        )
      )
    )
  );

  // Search Messages Effect
  searchMessages$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.searchMessages),
      mergeMap((action) =>
        this.chatService.searchMessages(action.query, action.page).pipe(
          map((results) =>
            ChatActions.searchMessagesSuccess({ results })
          ),
          catchError((error) =>
            of(ChatActions.searchMessagesFailure({ error }))
          )
        )
      )
    )
  );

  // Load Message History Effect
  loadMessageHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.loadMessageHistory),
      mergeMap((action) =>
        this.chatService.getMessageHistory(action.messageId).pipe(
          map((history) =>
            ChatActions.loadMessageHistorySuccess({ history })
          ),
          catchError((error) =>
            of(ChatActions.loadMessageHistoryFailure({ error }))
          )
        )
      )
    )
  );

  // Send Message Effect
  sendMessage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.sendMessage),
      withLatestFrom(this.store.select(selectChatState)),
      mergeMap(([action, state]) => {
        const history = action.parentId ? state.threadMessages : [];
        const modelConfig = this.modelConfigService.getModelConfig(action.role);
        const initialAiMessage: Message = {
          id: Date.now().toString(),
          role: 'assistant',
          content: '',
          timestamp: new Date()
        };

        return concat(
          of(ChatActions.sendMessageSuccess({ message: initialAiMessage })),
          this.aiService.generateContentStream(action.content, history, modelConfig).pipe(
            map((chunk) => ChatActions.streamMessageChunk({ chunk })),
            catchError((error) =>
              concat(
                of(ChatActions.sendMessageFailure({ error })),
                of(ChatActions.streamMessageChunk({ chunk: 'Sorry, I am having trouble connecting to the AI. Please try again later.' })),
                of(ChatActions.streamMessageEnd())
              )
            )
          ),
          of(ChatActions.streamMessageEnd())
        );
      })
    )
  );

  // Load Thread Messages Effect
  loadThreadMessages$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.loadThreadMessages),
      mergeMap((action) =>
        this.chatService.getReplies(action.messageId).pipe(
          map((messages) =>
            ChatActions.loadThreadMessagesSuccess({ messages })
          ),
          catchError((error) =>
            of(ChatActions.loadThreadMessagesFailure({ error }))
          )
        )
      )
    )
  );

  // Typing Effects
  startTyping$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.startTyping),
      mergeMap(() => {
        this.chatService.startTyping();
        return of(ChatActions.updateTypingUsers({ users: ['currentUser'] }));
      })
    )
  );

  stopTyping$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.stopTyping),
      mergeMap(() => {
        this.chatService.stopTyping();
        return of(ChatActions.updateTypingUsers({ users: [] }));
      })
    )
  );

  constructor(
    private actions$: Actions,
    private store: Store,
    private chatService: ChatService,
    private aiService: AiService,
    private modelConfigService: ModelConfigService
  ) {}
}
