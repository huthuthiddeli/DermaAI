import mongoose, {Schema, Document} from "mongoose";
import paginate from 'mongoose-paginate-v2';



var thingSchema = new Schema<IArticledata>({
    data:{
        type: Schema.Types.Mixed,
        required: true
    }
},
{
    collection: "test"
}
);

thingSchema.plugin(paginate);

interface IArticledata extends Document {
    data: any;
}


const dataModel = mongoose.model<IArticledata>('articel', thingSchema, 'articles')


export {thingSchema, dataModel}