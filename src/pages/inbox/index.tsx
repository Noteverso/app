import { TextEditor } from '@/features/editor'
import { NoteCard } from '@/features/note'

const content = '<h1>good morning</h1><h2>good morning</h2><h3>good morning</h3><p>good morning</p><p>list</p><ol><li><p>one</p></li><li><p>two</p></li><li><p>three</p></li></ol><p>ordered list</p><ul><li><p>good</p></li><li><p>better</p></li><li><p>best</p></li></ul><h2>task</h2><ul data-type="taskList"><li data-checked="false" data-type="taskItem"><label><input type="checkbox"><span></span></label><div><p>todo 1</p></div></li><li data-checked="false" data-type="taskItem"><label><input type="checkbox"><span></span></label><div><p>todo2</p><ul data-type="taskList"><li data-checked="false" data-type="taskItem"><label><input type="checkbox"><span></span></label><div><p>todo2-1</p></div></li><li data-checked="false" data-type="taskItem"><label><input type="checkbox"><span></span></label><div><p>todo2-2</p></div></li></ul></div></li><li data-checked="false" data-type="taskItem"><label><input type="checkbox"><span></span></label><div><p>todo 3</p></div></li></ul><p><code>code</code></p><pre><code class="language-js">console.log(123)</code></pre><blockquote><p>good morning</p></blockquote><p><mark><span style="color: #958DF1">Highlight</span></mark></p>'

export function Inbox() {
  return (
    <div className="app-inbox h-full overflow-hidden flex flex-col">
      <h1 className="text-2xl mb-4">收件箱</h1>
      <TextEditor />
      <ul className="flex flex-col gap-x-4">
        <li>
          <NoteCard
            tags={[{ name: 'tag11123123', tagId: '213' }, { name: 'tag124213', tagId: '123' }]}
            content={content}
            timeStamp="2022-01-01"
            project={{ projectId: '123', name: 'project1' }}
            fileNumber={1}
            linkedNoteNumber={1}
            linkingNoteNumber={1}
          />
        </li>
      </ul>

    </div >
  )
}
