import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as ChatActions from './features/chat/store/chat.actions';

@Component({
  selector: 'app-root',
  template: `
    <app-header></app-header>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
    <app-footer></app-footer>
  `,
  styles: []
})
export class AppComponent implements OnInit {
  title = 'MongoDB Chat Service';

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(ChatActions.loadMessages({}));
  }
}
