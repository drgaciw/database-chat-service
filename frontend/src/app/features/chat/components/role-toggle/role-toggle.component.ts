import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-role-toggle',
  templateUrl: './role-toggle.component.html',
  styleUrls: ['./role-toggle.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RoleToggleComponent {
  /**
   * The currently selected role.
   */
  @Input() selectedRole: 'Creative' | 'Precise' = 'Precise';
  /**
   * Emits when a new role is selected.
   */
  @Output() roleSelected = new EventEmitter<'Creative' | 'Precise'>();

  onRoleChange(role: 'Creative' | 'Precise'): void {
    this.roleSelected.emit(role);
  }
}
